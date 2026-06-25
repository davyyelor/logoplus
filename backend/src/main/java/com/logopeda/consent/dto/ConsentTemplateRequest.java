package com.logopeda.consent.dto;

import com.logopeda.consent.enums.ConsentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ConsentTemplateRequest(
        @NotBlank(message = "name is required")
        String name,

        @NotNull(message = "consentType is required")
        ConsentType consentType,

        @NotBlank(message = "body is required")
        String body) {
}
