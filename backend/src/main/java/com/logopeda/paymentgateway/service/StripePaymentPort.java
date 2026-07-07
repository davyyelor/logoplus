package com.logopeda.paymentgateway.service;

import com.logopeda.paymentgateway.dto.CheckoutRequest;
import com.logopeda.paymentgateway.dto.CheckoutSessionResult;
import com.logopeda.paymentgateway.dto.ConnectAccountResult;
import com.logopeda.paymentgateway.dto.WebhookResult;
import com.logopeda.paymentgateway.enums.StripeMode;

/**
 * Port for the Stripe payment gateway. Implementations are selected via
 * {@code app.stripe.mode}. All external-integration behaviour lives behind
 * this interface so the rest of the application never depends on Stripe
 * directly and can run fully with the gateway disabled.
 */
public interface StripePaymentPort {

    StripeMode mode();

    boolean isEnabled();

    CheckoutSessionResult createCheckoutSession(String clinicId, CheckoutRequest request);

    ConnectAccountResult createConnectOnboarding(String clinicId, String returnUrl);

    WebhookResult handleWebhook(String payload, String signatureHeader);
}
