package com.logopeda.document.service;

import com.logopeda.audit.service.AuditService;
import com.logopeda.billing.exception.BusinessValidationException;
import com.logopeda.billing.exception.ResourceNotFoundException;
import com.logopeda.document.dto.DocumentContent;
import com.logopeda.document.enums.DocumentType;
import com.logopeda.document.model.Document;
import com.logopeda.document.repository.DocumentRepository;
import com.logopeda.document.storage.StoragePort;
import com.logopeda.patient.model.Patient;
import com.logopeda.patient.service.PatientAccessGuard;
import com.logopeda.shared.config.AppProperties;
import com.logopeda.shared.enums.Role;
import com.logopeda.shared.security.TenantContext;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Stores and serves patient documents. Enforces allowed content types and a max
 * size (from configuration), keeps tenant isolation via {@link PatientAccessGuard}
 * and restricts FAMILY users to documents explicitly shared with them. File bytes
 * are delegated to a {@link StoragePort} with server-generated keys.
 */
@Service
@Transactional
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final StoragePort storagePort;
    private final PatientAccessGuard patientAccessGuard;
    private final TenantContext tenantContext;
    private final AuditService auditService;
    private final long maxFileSizeBytes;
    private final Set<String> allowedContentTypes;

    public DocumentService(DocumentRepository documentRepository, StoragePort storagePort,
                           PatientAccessGuard patientAccessGuard, TenantContext tenantContext,
                           AuditService auditService, AppProperties properties) {
        this.documentRepository = documentRepository;
        this.storagePort = storagePort;
        this.patientAccessGuard = patientAccessGuard;
        this.tenantContext = tenantContext;
        this.auditService = auditService;
        this.maxFileSizeBytes = properties.getStorage().getMaxFileSizeBytes();
        this.allowedContentTypes = Arrays.stream(
                        properties.getStorage().getAllowedContentTypes().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public List<Document> listForPatient(String patientId) {
        Patient patient = patientAccessGuard.requireAccessiblePatient(patientId);
        if (tenantContext.currentRole() == Role.FAMILY) {
            return documentRepository.findByPatientIdAndClinicIdAndVisibleToFamilyTrueOrderByCreatedAtDesc(
                    patientId, patient.getClinicId());
        }
        return documentRepository.findByPatientIdAndClinicIdOrderByCreatedAtDesc(patientId, patient.getClinicId());
    }

    /** Staff upload: visibility is chosen by the uploader. */
    public Document upload(String patientId, MultipartFile file, DocumentType type, boolean visibleToFamily) {
        Patient patient = patientAccessGuard.requireAccessiblePatient(patientId);
        return store(patient, file, type, visibleToFamily);
    }

    /**
     * Family upload: always stored as visible to the family that uploaded it.
     * Access is still gated by {@link PatientAccessGuard} so a family user can
     * only attach documents to their own linked patients.
     */
    public Document uploadByFamily(String patientId, MultipartFile file, DocumentType type) {
        Patient patient = patientAccessGuard.requireAccessiblePatient(patientId);
        return store(patient, file, type != null ? type : DocumentType.OTHER, true);
    }

    @Transactional(readOnly = true)
    public DocumentContent download(String id) {
        Document document = getAccessible(id);
        byte[] bytes = storagePort.load(document.getStorageKey());
        auditService.record("DOCUMENT_DOWNLOADED", "Document", document.getId(), null);
        return new DocumentContent(document.getOriginalFileName(), document.getContentType(), bytes);
    }

    public Document setVisibleToFamily(String id, boolean visible) {
        String clinicId = tenantContext.requireClinicId();
        Document document = documentRepository.findByIdAndClinicId(id, clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Document", id));
        document.setVisibleToFamily(visible);
        Document saved = documentRepository.save(document);
        auditService.record(visible ? "DOCUMENT_SHARED_WITH_FAMILY" : "DOCUMENT_HIDDEN_FROM_FAMILY",
                "Document", saved.getId(), null);
        return saved;
    }

    public void delete(String id) {
        String clinicId = tenantContext.requireClinicId();
        Document document = documentRepository.findByIdAndClinicId(id, clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Document", id));
        storagePort.delete(document.getStorageKey());
        documentRepository.delete(document);
        auditService.record("DOCUMENT_DELETED", "Document", id, null);
    }

    private Document store(Patient patient, MultipartFile file, DocumentType type, boolean visibleToFamily) {
        validate(file);
        String contentType = file.getContentType().toLowerCase();
        byte[] content;
        try {
            content = file.getBytes();
        } catch (java.io.IOException ex) {
            throw new BusinessValidationException("Could not read uploaded file");
        }
        String key = storagePort.store(patient.getClinicId(), content, contentType);

        Document document = new Document();
        document.setClinicId(patient.getClinicId());
        document.setPatientId(patient.getId());
        document.setDocumentType(type != null ? type : DocumentType.OTHER);
        document.setOriginalFileName(sanitizeFileName(file.getOriginalFilename()));
        document.setContentType(contentType);
        document.setSizeBytes(file.getSize());
        document.setStorageKey(key);
        document.setVisibleToFamily(visibleToFamily);
        document.setUploadedByUserId(tenantContext.currentUserId());

        Document saved = documentRepository.save(document);
        auditService.record("DOCUMENT_UPLOADED", "Document", saved.getId(),
                "{\"patientId\":\"" + patient.getId() + "\"}");
        return saved;
    }

    private Document getAccessible(String id) {
        String clinicId = tenantContext.requireClinicId();
        Document document = documentRepository.findByIdAndClinicId(id, clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Document", id));
        if (document.getPatientId() != null) {
            patientAccessGuard.requireAccessiblePatient(document.getPatientId());
        }
        if (tenantContext.currentRole() == Role.FAMILY && !document.isVisibleToFamily()) {
            // Hide existence from family users rather than returning 403.
            throw new ResourceNotFoundException("Document", id);
        }
        return document;
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessValidationException("File is required");
        }
        if (file.getSize() > maxFileSizeBytes) {
            throw new BusinessValidationException("File exceeds the maximum allowed size");
        }
        String contentType = file.getContentType();
        if (contentType == null || !allowedContentTypes.contains(contentType.toLowerCase())) {
            throw new BusinessValidationException("Unsupported file type: " + contentType);
        }
    }

    /** Strips any path components from a client-supplied name (display only). */
    private String sanitizeFileName(String name) {
        if (name == null || name.isBlank()) {
            return "document";
        }
        String stripped = name.replace("\\", "/");
        int slash = stripped.lastIndexOf('/');
        if (slash >= 0) {
            stripped = stripped.substring(slash + 1);
        }
        return stripped.isBlank() ? "document" : stripped;
    }
}
