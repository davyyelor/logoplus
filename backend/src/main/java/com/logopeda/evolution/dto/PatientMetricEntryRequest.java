package com.logopeda.evolution.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record PatientMetricEntryRequest(
        String sessionId,
        @NotNull Double value,
        @NotNull LocalDate entryDate,
        String notes) {
}
