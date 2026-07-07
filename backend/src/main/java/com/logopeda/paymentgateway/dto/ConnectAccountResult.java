package com.logopeda.paymentgateway.dto;

import com.logopeda.paymentgateway.enums.StripeMode;

/** Result of creating a Stripe Connect onboarding link. */
public record ConnectAccountResult(
        StripeMode mode,
        boolean enabled,
        String accountId,
        String onboardingUrl) {
}
