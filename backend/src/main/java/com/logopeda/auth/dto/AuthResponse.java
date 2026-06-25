package com.logopeda.auth.dto;

import com.logopeda.shared.enums.Role;

/** Returned after a successful login. */
public record AuthResponse(
        String token,
        String tokenType,
        long expiresInMinutes,
        String userId,
        String clinicId,
        String email,
        String firstName,
        String lastName,
        Role role) {
}
