package com.logopeda.billing.integration.provider;

import com.logopeda.billing.integration.IntegrationModels.ExternalPaymentRequest;
import com.logopeda.billing.integration.IntegrationModels.ExternalRefundRequest;
import com.logopeda.billing.integration.IntegrationModels.ProviderResult;
import com.logopeda.billing.integration.PaymentIntegrationPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * PLACEHOLDER for a future Stripe integration. Disabled by default and never
 * calls the real Stripe API.
 */
@Component
@ConditionalOnProperty(prefix = "billing.integrations.stripe", name = "enabled", havingValue = "true")
public class StripePaymentProvider implements PaymentIntegrationPort {

    @Override
    public ProviderResult registerExternalPayment(ExternalPaymentRequest request) {
        // TODO(future): create a Stripe PaymentIntent / charge. Intentionally not implemented.
        return ProviderResult.notImplemented(getProviderName());
    }

    @Override
    public ProviderResult refundExternalPayment(ExternalRefundRequest request) {
        // TODO(future): create a Stripe refund. Intentionally not implemented.
        return ProviderResult.notImplemented(getProviderName());
    }

    @Override
    public String getProviderName() {
        return "STRIPE";
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
