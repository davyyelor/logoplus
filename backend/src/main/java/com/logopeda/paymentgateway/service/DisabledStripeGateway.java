package com.logopeda.paymentgateway.service;

import com.logopeda.billing.exception.BusinessValidationException;
import com.logopeda.paymentgateway.dto.CheckoutRequest;
import com.logopeda.paymentgateway.dto.CheckoutSessionResult;
import com.logopeda.paymentgateway.dto.ConnectAccountResult;
import com.logopeda.paymentgateway.dto.WebhookResult;
import com.logopeda.paymentgateway.enums.StripeMode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Default gateway used when Stripe is disabled. It never contacts Stripe:
 * create operations are rejected and webhooks are acknowledged but ignored.
 */
@Component
@ConditionalOnProperty(prefix = "app.stripe", name = "mode", havingValue = "disabled",
        matchIfMissing = true)
public class DisabledStripeGateway implements StripePaymentPort {

    @Override
    public StripeMode mode() {
        return StripeMode.DISABLED;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public CheckoutSessionResult createCheckoutSession(String clinicId, CheckoutRequest request) {
        throw new BusinessValidationException("Stripe integration is disabled");
    }

    @Override
    public ConnectAccountResult createConnectOnboarding(String clinicId, String returnUrl) {
        throw new BusinessValidationException("Stripe integration is disabled");
    }

    @Override
    public WebhookResult handleWebhook(String payload, String signatureHeader) {
        return new WebhookResult(StripeMode.DISABLED, false, null);
    }
}
