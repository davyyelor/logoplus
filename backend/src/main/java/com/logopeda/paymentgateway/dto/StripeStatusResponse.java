package com.logopeda.paymentgateway.dto;

import com.logopeda.paymentgateway.enums.StripeMode;

/** Public status of the Stripe integration for the clinic UI. */
public record StripeStatusResponse(
        StripeMode mode,
        boolean enabled,
        double platformFeePercent) {
}
