package com.logopeda.guardian.dto;

import com.logopeda.guardian.enums.GuardianRelationship;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Payload to create or update a guardian. */
public record GuardianRequest(
        @NotBlank(message = "firstName is required")
        String firstName,

        @NotBlank(message = "lastName is required")
        String lastName,

        @NotNull(message = "relationship is required")
        GuardianRelationship relationship,

        @Email(message = "email must be valid")
        String email,

        @Size(max = 32)
        String phone,

        boolean canAccessPortal,

        boolean canReceiveReports,

        boolean canReceiveReminders) {
}
