package com.logopeda.consent.service;

import com.logopeda.audit.service.AuditService;
import com.logopeda.billing.exception.BusinessValidationException;
import com.logopeda.billing.exception.ResourceNotFoundException;
import com.logopeda.clinic.model.Clinic;
import com.logopeda.clinic.repository.ClinicRepository;
import com.logopeda.consent.dto.IssueConsentRequest;
import com.logopeda.consent.dto.SignConsentRequest;
import com.logopeda.consent.enums.ConsentStatus;
import com.logopeda.consent.model.ConsentTemplate;
import com.logopeda.consent.model.PatientConsent;
import com.logopeda.consent.repository.ConsentTemplateRepository;
import com.logopeda.consent.repository.PatientConsentRepository;
import com.logopeda.patient.model.Patient;
import com.logopeda.patient.service.PatientAccessGuard;
import com.logopeda.shared.pdf.PdfRenderer;
import com.logopeda.shared.security.TenantContext;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manages patient consents: issuing from a template, signing (typed electronic
 * signature), revoking and producing a PDF copy. All access is patient-scoped so
 * FAMILY users can only act on consents for patients they are linked to.
 */
@Service
@Transactional
public class PatientConsentService {

    private static final DateTimeFormatter DATE_TIME =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault());

    private final PatientConsentRepository consentRepository;
    private final ConsentTemplateRepository templateRepository;
    private final ClinicRepository clinicRepository;
    private final PatientAccessGuard patientAccessGuard;
    private final PdfRenderer pdfRenderer;
    private final TenantContext tenantContext;
    private final AuditService auditService;

    public PatientConsentService(PatientConsentRepository consentRepository,
                                 ConsentTemplateRepository templateRepository,
                                 ClinicRepository clinicRepository,
                                 PatientAccessGuard patientAccessGuard,
                                 PdfRenderer pdfRenderer,
                                 TenantContext tenantContext,
                                 AuditService auditService) {
        this.consentRepository = consentRepository;
        this.templateRepository = templateRepository;
        this.clinicRepository = clinicRepository;
        this.patientAccessGuard = patientAccessGuard;
        this.pdfRenderer = pdfRenderer;
        this.tenantContext = tenantContext;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<PatientConsent> listForPatient(String patientId) {
        Patient patient = patientAccessGuard.requireAccessiblePatient(patientId);
        return consentRepository.findByPatientIdAndClinicIdOrderByCreatedAtDesc(patientId, patient.getClinicId());
    }

    @Transactional(readOnly = true)
    public PatientConsent getById(String id) {
        String clinicId = tenantContext.requireClinicId();
        PatientConsent consent = findOwned(clinicId, id);
        patientAccessGuard.requireAccessiblePatient(consent.getPatientId());
        return consent;
    }

    /** Issues a PENDING consent for a patient from a template snapshot. */
    public PatientConsent issue(String patientId, IssueConsentRequest request) {
        Patient patient = patientAccessGuard.requireAccessiblePatient(patientId);
        String clinicId = patient.getClinicId();
        ConsentTemplate template = templateRepository.findByIdAndClinicId(request.templateId(), clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("ConsentTemplate", request.templateId()));

        PatientConsent consent = new PatientConsent();
        consent.setClinicId(clinicId);
        consent.setPatientId(patientId);
        consent.setTemplateId(template.getId());
        consent.setGuardianId(request.guardianId());
        consent.setConsentType(template.getConsentType());
        consent.setTitle(template.getName());
        consent.setBody(template.getBody());
        consent.setStatus(ConsentStatus.PENDING);
        consent.setExpiresOn(request.expiresOn());

        PatientConsent saved = consentRepository.save(consent);
        auditService.record("CONSENT_ISSUED", "PatientConsent", saved.getId(),
                "{\"patientId\":\"" + patientId + "\"}");
        return saved;
    }

    /** Records a signature. The typed name is the simple electronic signature. */
    public PatientConsent sign(String id, SignConsentRequest request) {
        String clinicId = tenantContext.requireClinicId();
        PatientConsent consent = findOwned(clinicId, id);
        patientAccessGuard.requireAccessiblePatient(consent.getPatientId());
        if (consent.getStatus() == ConsentStatus.REVOKED) {
            throw new BusinessValidationException("Cannot sign a revoked consent");
        }
        consent.setStatus(ConsentStatus.SIGNED);
        consent.setSignatureText(request.signatureText().trim());
        consent.setSignedByUserId(tenantContext.currentUserId());
        consent.setSignedAt(Instant.now());
        consent.setRevokedAt(null);
        PatientConsent saved = consentRepository.save(consent);
        auditService.record("CONSENT_SIGNED", "PatientConsent", saved.getId(), null);
        return saved;
    }

    public PatientConsent revoke(String id) {
        String clinicId = tenantContext.requireClinicId();
        PatientConsent consent = findOwned(clinicId, id);
        patientAccessGuard.requireAccessiblePatient(consent.getPatientId());
        consent.setStatus(ConsentStatus.REVOKED);
        consent.setRevokedAt(Instant.now());
        PatientConsent saved = consentRepository.save(consent);
        auditService.record("CONSENT_REVOKED", "PatientConsent", saved.getId(), null);
        return saved;
    }

    @Transactional(readOnly = true)
    public byte[] renderPdf(String id) {
        PatientConsent consent = getById(id);
        Clinic clinic = clinicRepository.findById(consent.getClinicId()).orElse(null);
        StringBuilder body = new StringBuilder(consent.getBody());
        body.append("\n\n----------------------------------------\n");
        body.append("Estado: ").append(consent.getStatus()).append("\n");
        if (consent.getSignatureText() != null) {
            body.append("Firmado por: ").append(consent.getSignatureText()).append("\n");
        }
        if (consent.getSignedAt() != null) {
            body.append("Fecha de firma: ").append(DATE_TIME.format(consent.getSignedAt())).append("\n");
        }
        String subtitle = clinic != null ? clinic.getName() : "";
        auditService.record("CONSENT_PDF_DOWNLOADED", "PatientConsent", consent.getId(), null);
        return pdfRenderer.render(consent.getTitle(), subtitle, body.toString());
    }

    private PatientConsent findOwned(String clinicId, String id) {
        return consentRepository.findByIdAndClinicId(id, clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("PatientConsent", id));
    }
}
