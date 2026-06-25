package com.logopeda.family.service;

import com.logopeda.appointment.model.Appointment;
import com.logopeda.appointment.repository.AppointmentRepository;
import com.logopeda.consent.model.PatientConsent;
import com.logopeda.consent.repository.PatientConsentRepository;
import com.logopeda.document.model.Document;
import com.logopeda.document.repository.DocumentRepository;
import com.logopeda.guardian.service.FamilyAccessService;
import com.logopeda.patient.model.Patient;
import com.logopeda.patient.repository.PatientRepository;
import com.logopeda.patient.service.PatientAccessGuard;
import com.logopeda.report.enums.ReportStatus;
import com.logopeda.report.model.GeneratedReport;
import com.logopeda.report.repository.GeneratedReportRepository;
import com.logopeda.shared.security.AuthenticatedUser;
import com.logopeda.shared.security.TenantContext;
import com.logopeda.therapy.model.TherapySession;
import com.logopeda.therapy.repository.TherapySessionRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Read model for the family portal. Every method resolves data only for patients
 * the authenticated FAMILY user is linked to. Patient-level access is enforced by
 * {@link PatientAccessGuard}; what families may see is deliberately narrowed
 * (only shared reports and family-visible documents).
 */
@Service
@Transactional(readOnly = true)
public class FamilyPortalService {

    private final FamilyAccessService familyAccessService;
    private final PatientAccessGuard patientAccessGuard;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TherapySessionRepository sessionRepository;
    private final DocumentRepository documentRepository;
    private final GeneratedReportRepository reportRepository;
    private final PatientConsentRepository consentRepository;
    private final TenantContext tenantContext;

    public FamilyPortalService(FamilyAccessService familyAccessService, PatientAccessGuard patientAccessGuard,
                               PatientRepository patientRepository, AppointmentRepository appointmentRepository,
                               TherapySessionRepository sessionRepository, DocumentRepository documentRepository,
                               GeneratedReportRepository reportRepository, PatientConsentRepository consentRepository,
                               TenantContext tenantContext) {
        this.familyAccessService = familyAccessService;
        this.patientAccessGuard = patientAccessGuard;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.sessionRepository = sessionRepository;
        this.documentRepository = documentRepository;
        this.reportRepository = reportRepository;
        this.consentRepository = consentRepository;
        this.tenantContext = tenantContext;
    }

    /** The patients the current family user is linked to. */
    public List<Patient> myPatients() {
        AuthenticatedUser user = tenantContext.requireCurrentUser();
        List<String> ids = familyAccessService.accessiblePatientIds(user.getUserId());
        if (ids.isEmpty()) {
            return List.of();
        }
        return patientRepository.findAllById(ids).stream()
                .filter(p -> user.getClinicId().equals(p.getClinicId()))
                .toList();
    }

    public List<Appointment> appointments(String patientId) {
        Patient patient = patientAccessGuard.requireAccessiblePatient(patientId);
        return appointmentRepository.findByPatientIdAndClinicIdOrderByStartDateTimeDesc(
                patientId, patient.getClinicId());
    }

    /** Sessions that carry homework — what families typically care about. */
    public List<TherapySession> sessions(String patientId) {
        Patient patient = patientAccessGuard.requireAccessiblePatient(patientId);
        return sessionRepository.findByPatientIdAndClinicIdOrderBySessionDateDesc(patientId, patient.getClinicId());
    }

    /** Only documents explicitly marked visible to the family. */
    public List<Document> documents(String patientId) {
        Patient patient = patientAccessGuard.requireAccessiblePatient(patientId);
        return documentRepository.findByPatientIdAndClinicIdAndVisibleToFamilyTrueOrderByCreatedAtDesc(
                patientId, patient.getClinicId());
    }

    /** Only reports that were shared with the family. */
    public List<GeneratedReport> reports(String patientId) {
        Patient patient = patientAccessGuard.requireAccessiblePatient(patientId);
        return reportRepository.findByPatientIdAndClinicIdOrderByCreatedAtDesc(patientId, patient.getClinicId())
                .stream()
                .filter(r -> r.getStatus() == ReportStatus.SHARED_WITH_FAMILY)
                .toList();
    }

    public List<PatientConsent> consents(String patientId) {
        Patient patient = patientAccessGuard.requireAccessiblePatient(patientId);
        return consentRepository.findByPatientIdAndClinicIdOrderByCreatedAtDesc(patientId, patient.getClinicId());
    }
}
