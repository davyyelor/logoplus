package com.logopeda.paymentgateway.dto;

import com.logopeda.paymentgateway.enums.StripeMode;

/** Outcome of processing an inbound Stripe webhook event. */
public record WebhookResult(
        StripeMode mode,
        boolean handled,
        String eventType) {
}
