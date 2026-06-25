package com.logopeda.report.service;

import com.logopeda.audit.service.AuditService;
import com.logopeda.billing.exception.BusinessValidationException;
import com.logopeda.billing.exception.ResourceNotFoundException;
import com.logopeda.clinic.model.Clinic;
import com.logopeda.clinic.repository.ClinicRepository;
import com.logopeda.patient.model.Patient;
import com.logopeda.patient.service.PatientAccessGuard;
import com.logopeda.report.dto.GenerateReportRequest;
import com.logopeda.report.dto.UpdateReportRequest;
import com.logopeda.report.enums.ReportStatus;
import com.logopeda.report.model.GeneratedReport;
import com.logopeda.report.model.ReportTemplate;
import com.logopeda.report.repository.GeneratedReportRepository;
import com.logopeda.report.repository.ReportTemplateRepository;
import com.logopeda.shared.pdf.PdfRenderer;
import com.logopeda.shared.security.TenantContext;
import com.logopeda.therapy.model.TherapyGoal;
import com.logopeda.therapy.model.TherapySession;
import com.logopeda.therapy.repository.TherapyGoalRepository;
import com.logopeda.therapy.repository.TherapySessionRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Generates and manages patient reports. A report is produced by substituting
 * patient/clinic variables into a template and optionally appending a summary of
 * sessions and goals that the clinician selected. The clinician can then edit the
 * draft text before sharing. No clinical interpretation, diagnosis or
 * recommendation is produced automatically — only data the user recorded.
 */
@Service
@Transactional
public class GeneratedReportService {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final GeneratedReportRepository reportRepository;
    private final ReportTemplateRepository templateRepository;
    private final ClinicRepository clinicRepository;
    private final TherapySessionRepository sessionRepository;
    private final TherapyGoalRepository goalRepository;
    private final PatientAccessGuard patientAccessGuard;
    private final TemplateEngine templateEngine;
    private final PdfRenderer pdfRenderer;
    private final TenantContext tenantContext;
    private final AuditService auditService;

    public GeneratedReportService(GeneratedReportRepository reportRepository,
                                  ReportTemplateRepository templateRepository,
                                  ClinicRepository clinicRepository,
                                  TherapySessionRepository sessionRepository,
                                  TherapyGoalRepository goalRepository,
                                  PatientAccessGuard patientAccessGuard,
                                  TemplateEngine templateEngine,
                                  PdfRenderer pdfRenderer,
                                  TenantContext tenantContext,
                                  AuditService auditService) {
        this.reportRepository = reportRepository;
        this.templateRepository = templateRepository;
        this.clinicRepository = clinicRepository;
        this.sessionRepository = sessionRepository;
        this.goalRepository = goalRepository;
        this.patientAccessGuard = patientAccessGuard;
        this.templateEngine = templateEngine;
        this.pdfRenderer = pdfRenderer;
        this.tenantContext = tenantContext;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<GeneratedReport> listForPatient(String patientId) {
        Patient patient = patientAccessGuard.requireAccessiblePatient(patientId);
        return reportRepository.findByPatientIdAndClinicIdOrderByCreatedAtDesc(patientId, patient.getClinicId());
    }

    @Transactional(readOnly = true)
    public GeneratedReport getById(String id) {
        String clinicId = tenantContext.requireClinicId();
        GeneratedReport report = findOwned(clinicId, id);
        // Re-check patient access so FAMILY users cannot read other patients' reports.
        patientAccessGuard.requireAccessiblePatient(report.getPatientId());
        return report;
    }

    /** Builds a DRAFT report from a template plus the selected data. */
    public GeneratedReport generate(String patientId, GenerateReportRequest request) {
        Patient patient = patientAccessGuard.requireAccessiblePatient(patientId);
        String clinicId = patient.getClinicId();
        ReportTemplate template = templateRepository.findByIdAndClinicId(request.templateId(), clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("ReportTemplate", request.templateId()));
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic", clinicId));

        Map<String, String> variables = buildVariables(patient, clinic, request.recommendations());
        StringBuilder content = new StringBuilder(templateEngine.render(template.getContentTemplate(), variables));

        if (request.includeSessions()) {
            content.append("\n\n").append(buildSessionsSection(patient, request.sessionsFrom(), request.sessionsTo()));
        }
        if (request.includeGoals()) {
            content.append("\n\n").append(buildGoalsSection(patient, request.goalIds()));
        }

        GeneratedReport report = new GeneratedReport();
        report.setClinicId(clinicId);
        report.setPatientId(patientId);
        report.setTemplateId(template.getId());
        report.setReportType(template.getReportType());
        report.setTitle(request.title() != null && !request.title().isBlank()
                ? request.title().trim()
                : template.getName() + " - " + patient.getFullName());
        report.setContent(content.toString());
        report.setStatus(ReportStatus.GENERATED);
        report.setGeneratedAt(Instant.now());
        report.setCreatedByUserId(tenantContext.currentUserId());

        GeneratedReport saved = reportRepository.save(report);
        auditService.record("REPORT_GENERATED", "GeneratedReport", saved.getId(),
                "{\"patientId\":\"" + patientId + "\"}");
        return saved;
    }

    public GeneratedReport update(String id, UpdateReportRequest request) {
        String clinicId = tenantContext.requireClinicId();
        GeneratedReport report = findOwned(clinicId, id);
        if (report.getStatus() == ReportStatus.ARCHIVED) {
            throw new BusinessValidationException("Cannot edit an archived report");
        }
        report.setTitle(request.title().trim());
        report.setContent(request.content());
        return reportRepository.save(report);
    }

    public GeneratedReport shareWithFamily(String id) {
        return changeStatus(id, ReportStatus.SHARED_WITH_FAMILY, "REPORT_SHARED_WITH_FAMILY");
    }

    public GeneratedReport archive(String id) {
        return changeStatus(id, ReportStatus.ARCHIVED, "REPORT_ARCHIVED");
    }

    /** Renders the current report content to a PDF (generated on demand). */
    @Transactional(readOnly = true)
    public byte[] renderPdf(String id) {
        GeneratedReport report = getById(id);
        Clinic clinic = clinicRepository.findById(report.getClinicId()).orElse(null);
        String subtitle = (clinic != null ? clinic.getName() : "")
                + "  ·  " + DATE.format(LocalDate.now());
        auditService.record("REPORT_PDF_DOWNLOADED", "GeneratedReport", report.getId(), null);
        return pdfRenderer.render(report.getTitle(), subtitle, report.getContent());
    }

    private GeneratedReport changeStatus(String id, ReportStatus status, String auditAction) {
        String clinicId = tenantContext.requireClinicId();
        GeneratedReport report = findOwned(clinicId, id);
        report.setStatus(status);
        GeneratedReport saved = reportRepository.save(report);
        auditService.record(auditAction, "GeneratedReport", saved.getId(), null);
        return saved;
    }

    private Map<String, String> buildVariables(Patient patient, Clinic clinic, String recommendations) {
        Map<String, String> values = new HashMap<>();
        values.put("patient.fullName", safe(patient.getFullName()));
        values.put("patient.firstName", safe(patient.getFirstName()));
        values.put("patient.lastName", safe(patient.getLastName()));
        values.put("patient.birthDate", patient.getBirthDate() != null ? DATE.format(patient.getBirthDate()) : "");
        values.put("patient.age", patient.getBirthDate() != null
                ? String.valueOf(Period.between(patient.getBirthDate(), LocalDate.now()).getYears())
                : "");
        values.put("patient.school", safe(patient.getSchoolName()));
        values.put("patient.reasonForConsultation", safe(patient.getReasonForConsultation()));
        values.put("clinic.name", safe(clinic.getName()));
        values.put("clinic.legalName", safe(clinic.getLegalName()));
        values.put("clinic.email", safe(clinic.getEmail()));
        values.put("clinic.phone", safe(clinic.getPhone()));
        values.put("today", DATE.format(LocalDate.now()));
        values.put("recommendations", safe(recommendations));
        return values;
    }

    private String buildSessionsSection(Patient patient, LocalDate from, LocalDate to) {
        List<TherapySession> sessions = (from != null && to != null)
                ? sessionRepository.findByPatientIdAndClinicIdAndSessionDateBetweenOrderBySessionDateAsc(
                        patient.getId(), patient.getClinicId(), from, to)
                : sessionRepository.findByPatientIdAndClinicIdOrderBySessionDateDesc(
                        patient.getId(), patient.getClinicId());
        StringBuilder section = new StringBuilder("SESIONES\n");
        if (sessions.isEmpty()) {
            section.append("Sin sesiones registradas en el periodo seleccionado.");
            return section.toString();
        }
        for (TherapySession session : sessions) {
            section.append("- ").append(DATE.format(session.getSessionDate()));
            if (session.getSummary() != null && !session.getSummary().isBlank()) {
                section.append(": ").append(session.getSummary());
            }
            section.append("\n");
        }
        return section.toString().stripTrailing();
    }

    private String buildGoalsSection(Patient patient, List<String> goalIds) {
        List<TherapyGoal> goals = goalRepository.findByPatientIdAndClinicIdOrderByCreatedAtDesc(
                patient.getId(), patient.getClinicId());
        StringBuilder section = new StringBuilder("OBJETIVOS TERAPÉUTICOS\n");
        boolean any = false;
        for (TherapyGoal goal : goals) {
            if (goalIds != null && !goalIds.isEmpty() && !goalIds.contains(goal.getId())) {
                continue;
            }
            any = true;
            section.append("- ").append(goal.getTitle())
                    .append(" [").append(goal.getStatus()).append("]");
            if (goal.getArea() != null && !goal.getArea().isBlank()) {
                section.append(" (").append(goal.getArea()).append(")");
            }
            section.append("\n");
        }
        if (!any) {
            section.append("Sin objetivos registrados.");
        }
        return section.toString().stripTrailing();
    }

    private GeneratedReport findOwned(String clinicId, String id) {
        return reportRepository.findByIdAndClinicId(id, clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("GeneratedReport", id));
    }

    private String safe(String value) {
        return value != null ? value : "";
    }
}
