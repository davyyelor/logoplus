package com.logopeda.evidence.service;

import com.logopeda.audit.service.AuditService;
import com.logopeda.billing.exception.BusinessValidationException;
import com.logopeda.billing.exception.ResourceNotFoundException;
import com.logopeda.document.enums.DocumentType;
import com.logopeda.document.model.Document;
import com.logopeda.document.service.DocumentService;
import com.logopeda.evidence.dto.FamilyEvidenceRequest;
import com.logopeda.evidence.enums.EvidenceReviewStatus;
import com.logopeda.evidence.enums.EvidenceType;
import com.logopeda.evidence.model.FamilyEvidence;
import com.logopeda.evidence.repository.FamilyEvidenceRepository;
import com.logopeda.patient.service.PatientAccessGuard;
import com.logopeda.shared.security.TenantContext;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Family evidence management. Families create evidence (text or file) for their
 * linked patients; staff review it. File bytes are delegated to the existing
 * {@link DocumentService}/StoragePort so downloads reuse the document endpoint.
 * All access is scoped by clinic and {@link PatientAccessGuard}.
 */
@Service
@Transactional
public class FamilyEvidenceService {

    private final FamilyEvidenceRepository evidenceRepository;
    private final DocumentService documentService;
    private final PatientAccessGuard patientAccessGuard;
    private final TenantContext tenantContext;
    private final AuditService auditService;

    public FamilyEvidenceService(FamilyEvidenceRepository evidenceRepository, DocumentService documentService,
                                 PatientAccessGuard patientAccessGuard, TenantContext tenantContext,
                                 AuditService auditService) {
        this.evidenceRepository = evidenceRepository;
        this.documentService = documentService;
        this.patientAccessGuard = patientAccessGuard;
        this.tenantContext = tenantContext;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<FamilyEvidence> listForPatient(String patientId) {
        patientAccessGuard.requireAccessiblePatient(patientId);
        return evidenceRepository.findByPatientIdAndClinicIdOrderByCreatedAtDesc(
                patientId, tenantContext.requireClinicId());
    }

    /** Staff view of all evidence pending review across the clinic. */
    @Transactional(readOnly = true)
    public List<FamilyEvidence> listPendingReview() {
        return evidenceRepository.findByClinicIdAndReviewStatusOrderByCreatedAtDesc(
                tenantContext.requireClinicId(), EvidenceReviewStatus.PENDING_REVIEW);
    }

    /** Family creates a text-note evidence. */
    public FamilyEvidence createTextNote(String patientId, FamilyEvidenceRequest request) {
        patientAccessGuard.requireAccessiblePatient(patientId);
        if (request.textContent() == null || request.textContent().isBlank()) {
            throw new BusinessValidationException("Text content is required for a note");
        }
        FamilyEvidence evidence = baseEvidence(patientId, request);
        evidence.setType(EvidenceType.TEXT_NOTE);
        evidence.setTextContent(request.textContent());
        FamilyEvidence saved = evidenceRepository.save(evidence);
        auditService.record("FAMILY_EVIDENCE_CREATED", "FamilyEvidence", saved.getId(), null);
        return saved;
    }

    /** Family uploads a file as evidence. */
    public FamilyEvidence createUpload(String patientId, MultipartFile file, FamilyEvidenceRequest request) {
        patientAccessGuard.requireAccessiblePatient(patientId);
        Document document = documentService.uploadByFamily(patientId, file, DocumentType.OTHER);
        FamilyEvidence evidence = baseEvidence(patientId, request);
        evidence.setType(request.type() != null ? request.type() : EvidenceType.DOCUMENT);
        evidence.setStorageDocumentId(document.getId());
        evidence.setTextContent(request.textContent());
        FamilyEvidence saved = evidenceRepository.save(evidence);
        auditService.record("FAMILY_EVIDENCE_UPLOADED", "FamilyEvidence", saved.getId(), null);
        return saved;
    }

    /** Staff reviews (accepts/rejects) a piece of evidence. */
    public FamilyEvidence review(String id, EvidenceReviewStatus status, String reviewNote) {
        if (status == EvidenceReviewStatus.PENDING_REVIEW) {
            throw new BusinessValidationException("Review status must be REVIEWED or REJECTED");
        }
        FamilyEvidence evidence = evidenceRepository.findByIdAndClinicId(id, tenantContext.requireClinicId())
                .orElseThrow(() -> new ResourceNotFoundException("FamilyEvidence", id));
        patientAccessGuard.requireAccessiblePatient(evidence.getPatientId());
        evidence.setReviewStatus(status);
        evidence.setReviewedByUserId(tenantContext.currentUserId());
        evidence.setReviewedAt(Instant.now());
        if (reviewNote != null && !reviewNote.isBlank()) {
            evidence.setDescription(reviewNote);
        }
        FamilyEvidence saved = evidenceRepository.save(evidence);
        auditService.record("FAMILY_EVIDENCE_REVIEWED", "FamilyEvidence", saved.getId(),
                "{\"status\":\"" + status + "\"}");
        return saved;
    }

    private FamilyEvidence baseEvidence(String patientId, FamilyEvidenceRequest request) {
        FamilyEvidence evidence = new FamilyEvidence();
        evidence.setClinicId(tenantContext.requireClinicId());
        evidence.setPatientId(patientId);
        evidence.setUploadedByUserId(tenantContext.currentUserId());
        evidence.setTitle(request.title());
        evidence.setDescription(request.description());
        evidence.setReviewStatus(EvidenceReviewStatus.PENDING_REVIEW);
        if (request.homeworkId() != null && !request.homeworkId().isBlank()) {
            evidence.setHomeworkId(request.homeworkId());
        }
        return evidence;
    }
}
