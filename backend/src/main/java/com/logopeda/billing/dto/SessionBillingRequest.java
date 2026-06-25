package com.logopeda.billing.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Payload to create or update a session billing record. {@code paidAmount} is
 * optional on input and defaults to zero; status transitions are handled via
 * the dedicated mark-* endpoints.
 */
public record SessionBillingRequest(
        @NotNull(message = "patientId is required")
        @Size(min = 1, message = "patientId is required")
        String patientId,

        @NotNull(message = "sessionId is required")
        @Size(min = 1, message = "sessionId is required")
        String sessionId,

        @NotNull(message = "sessionDate is required")
        LocalDate sessionDate,

        @NotNull(message = "amount is required")
        @DecimalMin(value = "0.00", message = "amount cannot be negative")
        BigDecimal amount,

        @Size(min = 3, max = 3, message = "currency must be a 3-letter ISO code")
        String currency,

        @DecimalMin(value = "0.00", message = "paidAmount cannot be negative")
        BigDecimal paidAmount,

        @Size(max = 1000, message = "notes too long")
        String notes) {
}
