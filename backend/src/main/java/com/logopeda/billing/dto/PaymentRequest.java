package com.logopeda.billing.dto;

import com.logopeda.billing.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Payload to create or update a payment. */
public record PaymentRequest(
        @NotNull(message = "patientId is required")
        @Size(min = 1, message = "patientId is required")
        String patientId,

        String sessionId,

        String feeId,

        @NotNull(message = "amount is required")
        @DecimalMin(value = "0.01", message = "amount must be positive")
        BigDecimal amount,

        @Size(min = 3, max = 3, message = "currency must be a 3-letter ISO code")
        String currency,

        @NotNull(message = "paymentDate is required")
        LocalDate paymentDate,

        @NotNull(message = "method is required")
        PaymentMethod method,

        @Size(max = 1000, message = "notes too long")
        String notes) {
}
