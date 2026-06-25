package com.logopeda.billing.integration;

import com.logopeda.billing.integration.IntegrationModels.ExternalPaymentRequest;
import com.logopeda.billing.integration.IntegrationModels.ExternalRefundRequest;
import com.logopeda.billing.integration.IntegrationModels.ProviderResult;

/**
 * Port for future payment gateway integrations (e.g. Stripe).
 *
 * <p>This is an outbound port in the hexagonal sense: the core module depends on
 * this interface, not on any concrete gateway. No implementation in this MVP
 * performs real network calls.
 */
public interface PaymentIntegrationPort {

    ProviderResult registerExternalPayment(ExternalPaymentRequest request);

    ProviderResult refundExternalPayment(ExternalRefundRequest request);

    String getProviderName();

    /** Whether this provider is enabled. All providers are disabled in the MVP. */
    boolean isEnabled();
}
