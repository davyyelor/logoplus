package com.logopeda.evidence.mapper;

import com.logopeda.evidence.dto.FamilyEvidenceResponse;
import com.logopeda.evidence.model.FamilyEvidence;
import org.springframework.stereotype.Component;

@Component
public class FamilyEvidenceMapper {

    public FamilyEvidenceResponse toResponse(FamilyEvidence evidence) {
        return new FamilyEvidenceResponse(
                evidence.getId(),
                evidence.getClinicId(),
                evidence.getPatientId(),
                evidence.getHomeworkId(),
                evidence.getUploadedByUserId(),
                evidence.getType(),
                evidence.getTitle(),
                evidence.getDescription(),
                evidence.getStorageDocumentId(),
                evidence.getTextContent(),
                evidence.getReviewStatus(),
                evidence.getReviewedByUserId(),
                evidence.getReviewedAt(),
                evidence.getCreatedAt());
    }
}
