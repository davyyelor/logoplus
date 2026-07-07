package com.logopeda.signature.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request to electronically sign a document for a patient. The patient is taken
 * from the path; {@code documentType} labels what is being signed (e.g.
 * {@code CONSENT}, {@code REPORT}), {@code documentId} optionally references the
 * signed entity and {@code signerName} is the typed signature of the signer.
 */
public record SignatureRequest(
        @NotBlank @Size(max = 40) String documentType,
        @Size(max = 36) String documentId,
        @NotBlank @Size(max = 255) String signerName,
        @Size(max = 1000) String note) {
}
