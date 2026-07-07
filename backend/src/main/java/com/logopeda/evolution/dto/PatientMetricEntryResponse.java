package com.logopeda.evolution.dto;

import java.time.Instant;
import java.time.LocalDate;

public record PatientMetricEntryResponse(
        String id,
        String clinicId,
        String patientId,
        String metricId,
        String sessionId,
        Double value,
        LocalDate entryDate,
        String notes,
        Instant createdAt) {
}
