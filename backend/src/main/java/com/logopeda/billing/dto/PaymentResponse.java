package com.logopeda.billing.dto;

import com.logopeda.billing.enums.PaymentMethod;
import com.logopeda.billing.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/** Read model returned to clients for a payment. */
public record PaymentResponse(
        String id,
        String clinicId,
        String patientId,
        String sessionId,
        String feeId,
        BigDecimal amount,
        String currency,
        LocalDate paymentDate,
        PaymentMethod method,
        PaymentStatus status,
        String notes,
        Instant createdAt,
        Instant updatedAt) {
}
