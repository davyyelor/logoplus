package com.logopeda.document.model;

import com.logopeda.document.enums.DocumentType;
import com.logopeda.shared.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/**
 * Metadata for a stored file. The actual bytes live in the storage backend
 * (local disk in V1) referenced by {@code storageKey}; the original client file
 * name is kept only for display and is never used to build a filesystem path.
 */
@Entity
@Table(name = "documents", indexes = {
        @Index(name = "idx_documents_clinic", columnList = "clinicId"),
        @Index(name = "idx_documents_patient", columnList = "patientId")
})
public class Document extends BaseEntity {

    @Column(nullable = false, length = 36)
    private String clinicId;

    /** Documents may be patient-scoped or clinic-level (patientId null). */
    @Column(length = 36)
    private String patientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private DocumentType documentType = DocumentType.OTHER;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false, length = 128)
    private String contentType;

    @Column(nullable = false)
    private long sizeBytes;

    /** Opaque storage key (e.g. relative path under the storage root). */
    @Column(nullable = false)
    private String storageKey;

    @Column(nullable = false)
    private boolean visibleToFamily = false;

    @Column(length = 36)
    private String uploadedByUserId;

    public String getClinicId() {
        return clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public String getStorageKey() {
        return storageKey;
    }

    public void setStorageKey(String storageKey) {
        this.storageKey = storageKey;
    }

    public boolean isVisibleToFamily() {
        return visibleToFamily;
    }

    public void setVisibleToFamily(boolean visibleToFamily) {
        this.visibleToFamily = visibleToFamily;
    }

    public String getUploadedByUserId() {
        return uploadedByUserId;
    }

    public void setUploadedByUserId(String uploadedByUserId) {
        this.uploadedByUserId = uploadedByUserId;
    }
}
