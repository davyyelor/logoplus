package com.logopeda.evidence.dto;

import com.logopeda.evidence.enums.EvidenceReviewStatus;
import com.logopeda.evidence.enums.EvidenceType;
import java.time.Instant;

public record FamilyEvidenceResponse(
        String id,
        String clinicId,
        String patientId,
        String homeworkId,
        String uploadedByUserId,
        EvidenceType type,
        String title,
        String description,
        String storageDocumentId,
        String textContent,
        EvidenceReviewStatus reviewStatus,
        String reviewedByUserId,
        Instant reviewedAt,
        Instant createdAt) {
}
