package com.logopeda.guardian.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Creates a FAMILY portal account for an existing guardian. The temporary
 * password is returned once so the clinic can hand it to the family securely.
 */
public record CreateFamilyAccessRequest(
        @NotBlank(message = "temporaryPassword is required")
        @Size(min = 8, message = "temporaryPassword must be at least 8 characters")
        String temporaryPassword) {
}
