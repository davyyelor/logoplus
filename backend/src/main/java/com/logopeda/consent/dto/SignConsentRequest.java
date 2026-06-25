package com.logopeda.consent.dto;

import jakarta.validation.constraints.NotBlank;

/** Signs a consent. The typed full name acts as a simple electronic signature. */
public record SignConsentRequest(
        @NotBlank(message = "signatureText is required")
        String signatureText) {
}
