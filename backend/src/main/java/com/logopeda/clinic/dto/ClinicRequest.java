package com.logopeda.clinic.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Editable clinic fields. */
public record ClinicRequest(
        @NotBlank(message = "name is required")
        String name,

        String legalName,

        @Size(max = 32)
        String taxId,

        @Email(message = "email must be valid")
        String email,

        @Size(max = 32)
        String phone,

        String address,

        String city,

        String province,

        @Size(max = 16)
        String postalCode,

        @Size(min = 2, max = 2, message = "country must be a 2-letter ISO code")
        String country) {
}
