package com.logopeda.paymentgateway.dto;

import com.logopeda.paymentgateway.enums.StripeMode;

/** Result of opening a Stripe Checkout session. */
public record CheckoutSessionResult(
        StripeMode mode,
        boolean enabled,
        String sessionId,
        String checkoutUrl) {
}
