package com.logopeda.evidence.model;

import com.logopeda.evidence.enums.EvidenceReviewStatus;
import com.logopeda.evidence.enums.EvidenceType;
import com.logopeda.shared.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;

/** Evidence uploaded by a family member, linked to a patient and optionally a task. */
@Entity
@Table(name = "family_evidence", indexes = {
        @Index(name = "idx_evidence_clinic", columnList = "clinicId"),
        @Index(name = "idx_evidence_patient", columnList = "patientId"),
        @Index(name = "idx_evidence_review", columnList = "reviewStatus")
})
public class FamilyEvidence extends BaseEntity {

    @Column(nullable = false, length = 36)
    private String clinicId;

    @Column(nullable = false, length = 36)
    private String patientId;

    @Column(length = 36)
    private String homeworkId;

    @Column(length = 36)
    private String uploadedByUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private EvidenceType type = EvidenceType.TEXT_NOTE;

    private String title;

    @Column(length = 2000)
    private String description;

    @Column(length = 36)
    private String storageDocumentId;

    @Column(length = 4000)
    private String textContent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private EvidenceReviewStatus reviewStatus = EvidenceReviewStatus.PENDING_REVIEW;

    @Column(length = 36)
    private String reviewedByUserId;

    private Instant reviewedAt;

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

    public String getHomeworkId() {
        return homeworkId;
    }

    public void setHomeworkId(String homeworkId) {
        this.homeworkId = homeworkId;
    }

    public String getUploadedByUserId() {
        return uploadedByUserId;
    }

    public void setUploadedByUserId(String uploadedByUserId) {
        this.uploadedByUserId = uploadedByUserId;
    }

    public EvidenceType getType() {
        return type;
    }

    public void setType(EvidenceType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStorageDocumentId() {
        return storageDocumentId;
    }

    public void setStorageDocumentId(String storageDocumentId) {
        this.storageDocumentId = storageDocumentId;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public EvidenceReviewStatus getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(EvidenceReviewStatus reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public String getReviewedByUserId() {
        return reviewedByUserId;
    }

    public void setReviewedByUserId(String reviewedByUserId) {
        this.reviewedByUserId = reviewedByUserId;
    }

    public Instant getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(Instant reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
}
