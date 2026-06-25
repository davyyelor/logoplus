package com.logopeda.consent.dto;

import com.logopeda.consent.enums.ConsentStatus;
import com.logopeda.consent.enums.ConsentType;
import java.time.Instant;
import java.time.LocalDate;

public record PatientConsentResponse(
        String id,
        String clinicId,
        String patientId,
        String templateId,
        String guardianId,
        ConsentType consentType,
        String title,
        String body,
        ConsentStatus status,
        String signatureText,
        String signedByUserId,
        Instant signedAt,
        Instant revokedAt,
        LocalDate expiresOn,
        Instant createdAt,
        Instant updatedAt) {
}
