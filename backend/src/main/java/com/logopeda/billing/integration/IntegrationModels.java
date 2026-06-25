package com.logopeda.billing.integration;

import java.math.BigDecimal;

/**
 * Neutral request/response value objects shared by the future integration
 * providers. They are deliberately provider-agnostic so the core billing module
 * never depends on any specific third-party SDK.
 */
public final class IntegrationModels {

    private IntegrationModels() {
    }

    public record ExternalInvoiceRequest(
            String clinicId,
            String patientId,
            BigDecimal amount,
            String currency,
            String concept) {
    }

    public record ExternalPaymentRequest(
            String clinicId,
            String paymentId,
            BigDecimal amount,
            String currency,
            String method) {
    }

    public record ExternalRefundRequest(
            String clinicId,
            String externalPaymentId,
            BigDecimal amount,
            String currency) {
    }

    /** Generic result envelope for any provider operation. */
    public record ProviderResult(
            boolean success,
            String externalReference,
            String message) {

        public static ProviderResult notImplemented(String providerName) {
            return new ProviderResult(false, null,
                    providerName + " integration is a placeholder and is not enabled in this MVP.");
        }
    }
}
