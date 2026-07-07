package com.logopeda.clinicalhistory.service;

import com.logopeda.appointment.model.Appointment;
import com.logopeda.appointment.repository.AppointmentRepository;
import com.logopeda.audit.service.AuditService;
import com.logopeda.clinic.model.Clinic;
import com.logopeda.clinic.repository.ClinicRepository;
import com.logopeda.homework.model.Homework;
import com.logopeda.homework.repository.HomeworkRepository;
import com.logopeda.patient.model.Patient;
import com.logopeda.patient.service.PatientAccessGuard;
import com.logopeda.shared.pdf.PdfRenderer;
import com.logopeda.therapy.model.TherapyGoal;
import com.logopeda.therapy.model.TherapySession;
import com.logopeda.therapy.repository.TherapyGoalRepository;
import com.logopeda.therapy.repository.TherapySessionRepository;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Assembles a patient's clinical history for export. Only data explicitly
 * recorded by staff is included — no interpretation, diagnosis or automated
 * recommendation is produced. Access is always mediated by
 * {@link PatientAccessGuard}, so tenant isolation and family-link rules apply.
 */
@Service
@Transactional(readOnly = true)
public class ClinicalHistoryExportService {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            .withZone(java.time.ZoneOffset.UTC);

    private final PatientAccessGuard patientAccessGuard;
    private final ClinicRepository clinicRepository;
    private final TherapySessionRepository sessionRepository;
    private final AppointmentRepository appointmentRepository;
    private final TherapyGoalRepository goalRepository;
    private final HomeworkRepository homeworkRepository;
    private final PdfRenderer pdfRenderer;
    private final AuditService auditService;

    public ClinicalHistoryExportService(PatientAccessGuard patientAccessGuard,
                                        ClinicRepository clinicRepository,
                                        TherapySessionRepository sessionRepository,
                                        AppointmentRepository appointmentRepository,
                                        TherapyGoalRepository goalRepository,
                                        HomeworkRepository homeworkRepository,
                                        PdfRenderer pdfRenderer,
                                        AuditService auditService) {
        this.patientAccessGuard = patientAccessGuard;
        this.clinicRepository = clinicRepository;
        this.sessionRepository = sessionRepository;
        this.appointmentRepository = appointmentRepository;
        this.goalRepository = goalRepository;
        this.homeworkRepository = homeworkRepository;
        this.pdfRenderer = pdfRenderer;
        this.auditService = auditService;
    }

    /** PDF summary of the patient's clinical history. */
    public byte[] exportPdf(String patientId) {
        Patient patient = patientAccessGuard.requireAccessiblePatient(patientId);
        Clinic clinic = clinicRepository.findById(patient.getClinicId()).orElse(null);
        auditService.record("CLINICAL_HISTORY_EXPORTED", "Patient", patientId, "{\"format\":\"pdf\"}");
        return pdfRenderer.render(
                "Historia clínica - " + patient.getFullName(),
                (clinic != null ? clinic.getName() : "") + "  ·  " + DATE.format(LocalDate.now()),
                buildSummaryBody(patient));
    }

    /** CSV listing of the patient's sessions. */
    public byte[] exportCsv(String patientId) {
        Patient patient = patientAccessGuard.requireAccessiblePatient(patientId);
        auditService.record("CLINICAL_HISTORY_EXPORTED", "Patient", patientId, "{\"format\":\"csv\"}");
        return sessionsCsv(patient).getBytes(StandardCharsets.UTF_8);
    }

    /** ZIP bundle with a PDF summary plus CSV datasets. */
    public byte[] exportZip(String patientId) {
        Patient patient = patientAccessGuard.requireAccessiblePatient(patientId);
        Clinic clinic = clinicRepository.findById(patient.getClinicId()).orElse(null);
        byte[] pdf = pdfRenderer.render(
                "Historia clínica - " + patient.getFullName(),
                (clinic != null ? clinic.getName() : "") + "  ·  " + DATE.format(LocalDate.now()),
                buildSummaryBody(patient));

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ZipOutputStream zip = new ZipOutputStream(out)) {
            writeEntry(zip, "resumen.pdf", pdf);
            writeEntry(zip, "sesiones.csv", sessionsCsv(patient).getBytes(StandardCharsets.UTF_8));
            writeEntry(zip, "citas.csv", appointmentsCsv(patient).getBytes(StandardCharsets.UTF_8));
            writeEntry(zip, "objetivos.csv", goalsCsv(patient).getBytes(StandardCharsets.UTF_8));
            writeEntry(zip, "tareas.csv", homeworkCsv(patient).getBytes(StandardCharsets.UTF_8));
            zip.finish();
            auditService.record("CLINICAL_HISTORY_EXPORTED", "Patient", patientId, "{\"format\":\"zip\"}");
            return out.toByteArray();
        } catch (IOException ex) {
            throw new UncheckedIOException("Failed to build clinical history archive", ex);
        }
    }

    private void writeEntry(ZipOutputStream zip, String name, byte[] content) throws IOException {
        zip.putNextEntry(new ZipEntry(name));
        zip.write(content);
        zip.closeEntry();
    }

    private String buildSummaryBody(Patient patient) {
        StringBuilder body = new StringBuilder();
        body.append("DATOS DEL PACIENTE\n");
        body.append("Nombre: ").append(safe(patient.getFullName())).append("\n");
        if (patient.getBirthDate() != null) {
            body.append("Fecha de nacimiento: ").append(DATE.format(patient.getBirthDate())).append("\n");
        }
        body.append("Estado: ").append(patient.getStatus()).append("\n");
        if (patient.getReasonForConsultation() != null && !patient.getReasonForConsultation().isBlank()) {
            body.append("Motivo de consulta: ").append(patient.getReasonForConsultation()).append("\n");
        }

        List<TherapyGoal> goals = goalRepository.findByPatientIdAndClinicIdOrderByCreatedAtDesc(
                patient.getId(), patient.getClinicId());
        body.append("\nOBJETIVOS TERAPÉUTICOS (").append(goals.size()).append(")\n");
        for (TherapyGoal goal : goals) {
            body.append("- ").append(safe(goal.getTitle())).append(" [").append(goal.getStatus()).append("]\n");
        }

        List<TherapySession> sessions = sessionRepository.findByPatientIdAndClinicIdOrderBySessionDateDesc(
                patient.getId(), patient.getClinicId());
        body.append("\nSESIONES (").append(sessions.size()).append(")\n");
        for (TherapySession session : sessions) {
            body.append("- ").append(DATE.format(session.getSessionDate()));
            if (session.getSummary() != null && !session.getSummary().isBlank()) {
                body.append(": ").append(session.getSummary());
            }
            body.append("\n");
        }
        return body.toString().stripTrailing();
    }

    private String sessionsCsv(Patient patient) {
        List<TherapySession> sessions = sessionRepository.findByPatientIdAndClinicIdOrderBySessionDateDesc(
                patient.getId(), patient.getClinicId());
        StringBuilder csv = new StringBuilder();
        csv.append("fecha,tipo,duracion_min,resumen,actividades,respuesta_paciente,observaciones\n");
        for (TherapySession s : sessions) {
            appendRow(csv,
                    s.getSessionDate() != null ? DATE.format(s.getSessionDate()) : "",
                    String.valueOf(s.getSessionType()),
                    s.getDurationMinutes() != null ? String.valueOf(s.getDurationMinutes()) : "",
                    s.getSummary(),
                    s.getActivitiesPerformed(),
                    s.getPatientResponse(),
                    s.getObservations());
        }
        return csv.toString();
    }

    private String appointmentsCsv(Patient patient) {
        List<Appointment> appointments = appointmentRepository
                .findByPatientIdAndClinicIdOrderByStartDateTimeDesc(patient.getId(), patient.getClinicId());
        StringBuilder csv = new StringBuilder();
        csv.append("inicio,fin,titulo,estado,ubicacion,notas\n");
        for (Appointment a : appointments) {
            appendRow(csv,
                    a.getStartDateTime() != null ? DATETIME.format(a.getStartDateTime()) : "",
                    a.getEndDateTime() != null ? DATETIME.format(a.getEndDateTime()) : "",
                    a.getTitle(),
                    String.valueOf(a.getStatus()),
                    String.valueOf(a.getLocationType()),
                    a.getNotes());
        }
        return csv.toString();
    }

    private String goalsCsv(Patient patient) {
        List<TherapyGoal> goals = goalRepository.findByPatientIdAndClinicIdOrderByCreatedAtDesc(
                patient.getId(), patient.getClinicId());
        StringBuilder csv = new StringBuilder();
        csv.append("titulo,area,estado,prioridad,fecha_inicio,fecha_objetivo,fecha_logro,descripcion\n");
        for (TherapyGoal g : goals) {
            appendRow(csv,
                    g.getTitle(),
                    g.getArea(),
                    String.valueOf(g.getStatus()),
                    String.valueOf(g.getPriority()),
                    g.getStartDate() != null ? DATE.format(g.getStartDate()) : "",
                    g.getTargetDate() != null ? DATE.format(g.getTargetDate()) : "",
                    g.getAchievedDate() != null ? DATE.format(g.getAchievedDate()) : "",
                    g.getDescription());
        }
        return csv.toString();
    }

    private String homeworkCsv(Patient patient) {
        List<Homework> homework = homeworkRepository.findByPatientIdAndClinicIdOrderByCreatedAtDesc(
                patient.getId(), patient.getClinicId());
        StringBuilder csv = new StringBuilder();
        csv.append("titulo,estado,fecha_limite,visible_familia,descripcion\n");
        for (Homework h : homework) {
            appendRow(csv,
                    h.getTitle(),
                    String.valueOf(h.getStatus()),
                    h.getDueDate() != null ? DATE.format(h.getDueDate()) : "",
                    String.valueOf(h.isVisibleToFamily()),
                    h.getDescription());
        }
        return csv.toString();
    }

    private void appendRow(StringBuilder csv, String... cells) {
        for (int i = 0; i < cells.length; i++) {
            if (i > 0) {
                csv.append(',');
            }
            csv.append(escape(cells[i]));
        }
        csv.append('\n');
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        String cleaned = value.replace("\r", " ").replace("\n", " ");
        if (cleaned.contains(",") || cleaned.contains("\"")) {
            return '"' + cleaned.replace("\"", "\"\"") + '"';
        }
        return cleaned;
    }

    private String safe(String value) {
        return value != null ? value : "";
    }
}
