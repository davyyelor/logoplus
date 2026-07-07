package com.logopeda.user.dto;

import com.logopeda.shared.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
/** Payload to create a user. Password is required on creation. */
public record CreateUserRequest(
        @NotBlank(message = "email is required")
        @Email(message = "email must be valid")
        String email,

        @NotBlank(message = "password is required")
        @Size(min = 8, message = "password must be at least 8 characters")
        String password,

        @NotBlank(message = "firstName is required")
        String firstName,

        @NotBlank(message = "lastName is required")
        String lastName,

        @NotNull(message = "role is required")
        Role role,

        @Size(max = 36)
        String centerId) {
}
