package com.logopeda.center.dto;

import java.time.Instant;

public record CenterResponse(
        String id,
        String clinicId,
        String name,
        String address,
        String city,
        String phone,
        String email,
        boolean active,
        Instant createdAt,
        Instant updatedAt) {
}
