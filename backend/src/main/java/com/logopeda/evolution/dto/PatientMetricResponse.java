package com.logopeda.evolution.dto;

import java.time.Instant;

public record PatientMetricResponse(
        String id,
        String clinicId,
        String patientId,
        String name,
        String description,
        String unit,
        boolean active,
        boolean visibleToFamily,
        Instant createdAt,
        Instant updatedAt) {
}
