package com.logopeda.clinic.dto;

import java.time.Instant;

public record ClinicResponse(
        String id,
        String name,
        String legalName,
        String taxId,
        String email,
        String phone,
        String address,
        String city,
        String province,
        String postalCode,
        String country,
        Instant createdAt,
        Instant updatedAt) {
}
