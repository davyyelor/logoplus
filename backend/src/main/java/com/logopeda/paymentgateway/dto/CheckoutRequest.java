package com.logopeda.paymentgateway.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** Request to open a Stripe Checkout session for a clinic payment. */
public record CheckoutRequest(
        @Size(max = 36)
        String patientId,

        @Size(max = 255)
        String description,

        @Positive(message = "amountCents must be positive")
        long amountCents,

        @Size(max = 3)
        String currency,

        @Size(max = 500)
        String successUrl,

        @Size(max = 500)
        String cancelUrl) {
}
