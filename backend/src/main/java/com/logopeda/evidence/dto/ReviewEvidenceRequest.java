package com.logopeda.evidence.dto;

import com.logopeda.evidence.enums.EvidenceReviewStatus;
import jakarta.validation.constraints.NotNull;

public record ReviewEvidenceRequest(
        @NotNull EvidenceReviewStatus reviewStatus,
        String reviewNote) {
}
