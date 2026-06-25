package com.logopeda.billing.dto;

import java.math.BigDecimal;

/** Aggregated financial summary for a clinic in an optional date range. */
public record BillingSummaryResponse(
        BigDecimal totalPaid,
        BigDecimal totalPending,
        BigDecimal totalRefunded,
        long numberOfPaidSessions,
        long numberOfPendingSessions,
        long activeFees,
        String currency) {
}
