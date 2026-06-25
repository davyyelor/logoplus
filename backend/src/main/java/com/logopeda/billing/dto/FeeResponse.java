package com.logopeda.billing.dto;

import com.logopeda.billing.enums.RecurrenceType;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/** Read model returned to clients for a fee. */
public record FeeResponse(
        String id,
        String clinicId,
        String patientId,
        String name,
        BigDecimal amount,
        String currency,
        RecurrenceType recurrenceType,
        LocalDate startDate,
        LocalDate endDate,
        boolean active,
        String notes,
        Instant createdAt,
        Instant updatedAt) {
}
