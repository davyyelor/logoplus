package com.logopeda.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Self-service registration of a new clinic together with its first admin user.
 * Creates the clinic and an active {@code CLINIC_ADMIN} in one step.
 */
public record RegisterClinicAdminRequest(
        @NotBlank(message = "clinicName is required")
        String clinicName,

        @NotBlank(message = "email is required")
        @Email(message = "email must be valid")
        String email,

        @NotBlank(message = "password is required")
        @Size(min = 8, message = "password must be at least 8 characters")
        String password,

        @NotBlank(message = "firstName is required")
        String firstName,

        @NotBlank(message = "lastName is required")
        String lastName) {
}
