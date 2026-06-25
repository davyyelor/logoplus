package com.logopeda.billing.integration;

import com.logopeda.billing.integration.IntegrationModels.ExternalInvoiceRequest;
import com.logopeda.billing.integration.IntegrationModels.ProviderResult;

/**
 * Port for future fiscal/legal compliance providers (e.g. TicketBAI, VeriFactu).
 *
 * <p>IMPORTANT: This MVP intentionally does NOT implement any real fiscal logic.
 * No legally valid invoices, signatures or tax submissions are produced. These
 * are placeholders to reserve the seam for a future compliant implementation.
 */
public interface FiscalProviderPort {

    /** Would register/sign a fiscal record. Placeholder only in this MVP. */
    ProviderResult submitFiscalRecord(ExternalInvoiceRequest request);

    String getProviderName();

    boolean isEnabled();
}
