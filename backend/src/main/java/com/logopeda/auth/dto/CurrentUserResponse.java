package com.logopeda.auth.dto;

import com.logopeda.shared.enums.Role;

/** Current authenticated user profile, returned by {@code GET /api/auth/me}. */
public record CurrentUserResponse(
        String userId,
        String clinicId,
        String email,
        String firstName,
        String lastName,
        Role role) {
}
