package com.logopeda.billing.dto;

import com.logopeda.billing.enums.SessionBillingStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/** Read model returned to clients for a session billing record. */
public record SessionBillingResponse(
        String id,
        String clinicId,
        String patientId,
        String sessionId,
        LocalDate sessionDate,
        BigDecimal amount,
        String currency,
        SessionBillingStatus status,
        BigDecimal paidAmount,
        BigDecimal pendingAmount,
        String paymentId,
        String notes,
        Instant createdAt,
        Instant updatedAt) {
}
