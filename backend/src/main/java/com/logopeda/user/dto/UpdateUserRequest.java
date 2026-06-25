package com.logopeda.user.dto;

import com.logopeda.shared.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Payload to update a user. Password is optional: when blank it is left
 * unchanged.
 */
public record UpdateUserRequest(
        @NotBlank(message = "firstName is required")
        String firstName,

        @NotBlank(message = "lastName is required")
        String lastName,

        @NotNull(message = "role is required")
        Role role,

        @Size(min = 8, message = "password must be at least 8 characters")
        String password) {
}
