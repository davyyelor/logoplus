package com.logopeda.user.dto;

import com.logopeda.shared.enums.Role;
import java.time.Instant;

/** Safe user representation: never exposes the password hash. */
public record UserResponse(
        String id,
        String clinicId,
        String email,
        String firstName,
        String lastName,
        Role role,
        boolean active,
        String centerId,
        Instant createdAt,
        Instant updatedAt) {
}
