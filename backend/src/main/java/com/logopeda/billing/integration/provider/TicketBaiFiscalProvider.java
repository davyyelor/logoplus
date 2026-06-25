package com.logopeda.billing.integration.provider;

import com.logopeda.billing.integration.FiscalProviderPort;
import com.logopeda.billing.integration.IntegrationModels.ExternalInvoiceRequest;
import com.logopeda.billing.integration.IntegrationModels.ProviderResult;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * PLACEHOLDER for a future TicketBAI fiscal integration.
 *
 * <p>This MVP does NOT implement any TicketBAI legal/fiscal logic. Disabled by
 * default and never produces real fiscal records.
 */
@Component
@ConditionalOnProperty(prefix = "billing.integrations.ticketbai", name = "enabled", havingValue = "true")
public class TicketBaiFiscalProvider implements FiscalProviderPort {

    @Override
    public ProviderResult submitFiscalRecord(ExternalInvoiceRequest request) {
        // TODO(future): implement TicketBAI compliant generation/submission. Not implemented.
        return ProviderResult.notImplemented(getProviderName());
    }

    @Override
    public String getProviderName() {
        return "TICKETBAI";
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
