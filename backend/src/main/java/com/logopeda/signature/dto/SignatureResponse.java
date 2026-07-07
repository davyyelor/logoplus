package com.logopeda.signature.dto;

import com.logopeda.signature.enums.SignatureProviderType;
import com.logopeda.signature.enums.SignatureStatus;
import java.time.Instant;

public record SignatureResponse(
        String id,
        String clinicId,
        String patientId,
        String documentType,
        String documentId,
        String signerUserId,
        String signerName,
        SignatureProviderType provider,
        SignatureStatus status,
        String signatureHash,
        String note,
        Instant signedAt,
        Instant createdAt,
        Instant updatedAt) {
}
