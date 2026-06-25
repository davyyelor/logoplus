package com.logopeda.billing.dto;

import com.logopeda.billing.enums.RecurrenceType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Payload to create or update a recurring fee. */
public record FeeRequest(
        @NotNull(message = "patientId is required")
        @Size(min = 1, message = "patientId is required")
        String patientId,

        @NotNull(message = "name is required")
        @Size(min = 1, max = 255, message = "name is required")
        String name,

        @NotNull(message = "amount is required")
        @DecimalMin(value = "0.01", message = "amount must be positive")
        BigDecimal amount,

        @Size(min = 3, max = 3, message = "currency must be a 3-letter ISO code")
        String currency,

        @NotNull(message = "recurrenceType is required")
        RecurrenceType recurrenceType,

        @NotNull(message = "startDate is required")
        LocalDate startDate,

        LocalDate endDate,

        @Size(max = 1000, message = "notes too long")
        String notes) {
}
