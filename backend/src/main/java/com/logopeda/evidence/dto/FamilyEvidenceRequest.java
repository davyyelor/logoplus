package com.logopeda.evidence.dto;

import com.logopeda.evidence.enums.EvidenceType;

/** Payload for creating a text-note evidence (no file). */
public record FamilyEvidenceRequest(
        String homeworkId,
        EvidenceType type,
        String title,
        String description,
        String textContent) {
}
