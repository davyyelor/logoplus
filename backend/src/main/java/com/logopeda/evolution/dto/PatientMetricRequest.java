package com.logopeda.evolution.dto;

import jakarta.validation.constraints.NotBlank;

public record PatientMetricRequest(
        @NotBlank String name,
        String description,
        String unit,
        Boolean active,
        Boolean visibleToFamily) {
}
