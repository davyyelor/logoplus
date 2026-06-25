package com.logopeda.document.dto;

import com.logopeda.document.enums.DocumentType;
import java.time.Instant;

public record DocumentResponse(
        String id,
        String clinicId,
        String patientId,
        DocumentType documentType,
        String originalFileName,
        String contentType,
        long sizeBytes,
        boolean visibleToFamily,
        String uploadedByUserId,
        Instant createdAt,
        Instant updatedAt) {
}
