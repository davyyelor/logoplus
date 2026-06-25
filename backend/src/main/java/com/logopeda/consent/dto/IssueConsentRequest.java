package com.logopeda.consent.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

/**
 * Issues a consent for a patient. The clinician selects a template (whose body
 * is snapshotted) and optionally a guardian who will sign and an expiry date.
 */
public record IssueConsentRequest(
        @NotBlank(message = "templateId is required")
        String templateId,

        String guardianId,

        LocalDate expiresOn) {
}
