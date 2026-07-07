package com.logopeda.signature.service;

import com.logopeda.audit.service.AuditService;
import com.logopeda.billing.exception.ResourceNotFoundException;
import com.logopeda.patient.model.Patient;
import com.logopeda.patient.service.PatientAccessGuard;
import com.logopeda.shared.security.TenantContext;
import com.logopeda.signature.dto.SignatureContext;
import com.logopeda.signature.dto.SignatureRequest;
import com.logopeda.signature.dto.SignatureResult;
import com.logopeda.signature.dto.SignatureStatusResponse;
import com.logopeda.signature.model.SignatureRecord;
import com.logopeda.signature.repository.SignatureRecordRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Creates and reads electronic signature records. All operations are
 * patient-scoped through {@link PatientAccessGuard} so FAMILY users can only act
 * on patients they are linked to, and every access is bounded by the tenant's
 * {@code clinicId}. Signing is delegated to the configured
 * {@link SignatureProviderPort}.
 */
@Service
@Transactional
public class SignatureService {

    private final SignatureRecordRepository repository;
    private final SignatureProviderPort provider;
    private final PatientAccessGuard patientAccessGuard;
    private final TenantContext tenantContext;
    private final AuditService auditService;

    public SignatureService(SignatureRecordRepository repository,
                            SignatureProviderPort provider,
                            PatientAccessGuard patientAccessGuard,
                            TenantContext tenantContext,
                            AuditService auditService) {
        this.repository = repository;
        this.provider = provider;
        this.patientAccessGuard = patientAccessGuard;
        this.tenantContext = tenantContext;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public SignatureStatusResponse status() {
        return new SignatureStatusResponse(provider.type(), provider.isEnabled());
    }

    @Transactional(readOnly = true)
    public List<SignatureRecord> listForPatient(String patientId) {
        Patient patient = patientAccessGuard.requireAccessiblePatient(patientId);
        return repository.findByPatientIdAndClinicIdOrderByCreatedAtDesc(patientId, patient.getClinicId());
    }

    @Transactional(readOnly = true)
    public SignatureRecord getById(String id) {
        String clinicId = tenantContext.requireClinicId();
        SignatureRecord record = repository.findByIdAndClinicId(id, clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("SignatureRecord", id));
        patientAccessGuard.requireAccessiblePatient(record.getPatientId());
        return record;
    }

    /** Signs a document for a patient using the configured provider. */
    public SignatureRecord sign(String patientId, SignatureRequest request) {
        Patient patient = patientAccessGuard.requireAccessiblePatient(patientId);
        String clinicId = patient.getClinicId();
        Instant signedAt = Instant.now();
        String documentId = StringUtils.hasText(request.documentId()) ? request.documentId().trim() : null;

        SignatureContext context = new SignatureContext(clinicId, patientId,
                request.documentType().trim(), documentId, request.signerName().trim(), signedAt);
        SignatureResult result = provider.sign(context);

        SignatureRecord record = new SignatureRecord();
        record.setClinicId(clinicId);
        record.setPatientId(patientId);
        record.setDocumentType(request.documentType().trim());
        record.setDocumentId(documentId);
        record.setSignerUserId(tenantContext.currentUserId());
        record.setSignerName(request.signerName().trim());
        record.setProvider(result.provider());
        record.setStatus(result.status());
        record.setSignatureHash(result.signatureHash());
        record.setNote(StringUtils.hasText(request.note()) ? request.note().trim() : null);
        record.setSignedAt(signedAt);

        SignatureRecord saved = repository.save(record);
        auditService.record("SIGNATURE_CREATED", "SignatureRecord", saved.getId(),
                "{\"patientId\":\"" + patientId + "\",\"provider\":\"" + result.provider() + "\"}");
        return saved;
    }
}
