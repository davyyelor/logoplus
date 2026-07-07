package com.logopeda.signature.dto;

import java.time.Instant;

/**
 * Immutable input passed to a {@link com.logopeda.signature.service.SignatureProviderPort}
 * describing the document being signed. Kept free of persistence concerns so
 * providers only see the data required to compute a signature.
 */
public record SignatureContext(
        String clinicId,
        String patientId,
        String documentType,
        String documentId,
        String signerName,
        Instant signedAt) {
}
