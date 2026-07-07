package com.logopeda.center.dto;

import jakarta.validation.constraints.NotBlank;

public record CenterRequest(
        @NotBlank String name,
        String address,
        String city,
        String phone,
        String email,
        Boolean active) {
}
