package com.logopeda.billing.integration;

import com.logopeda.billing.integration.IntegrationModels.ExternalInvoiceRequest;
import com.logopeda.billing.integration.IntegrationModels.ProviderResult;

/**
 * Port for future billing/invoicing providers (e.g. Holded, Quipu).
 *
 * <p>Placeholder only. No implementation issues real invoices in this MVP.
 */
public interface BillingProviderPort {

    ProviderResult createExternalInvoice(ExternalInvoiceRequest request);

    String getProviderName();

    boolean isEnabled();
}
