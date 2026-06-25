package com.logopeda.billing.integration.provider;

import com.logopeda.billing.integration.BillingProviderPort;
import com.logopeda.billing.integration.IntegrationModels.ExternalInvoiceRequest;
import com.logopeda.billing.integration.IntegrationModels.ProviderResult;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * PLACEHOLDER for a future Quipu integration. Disabled by default and never
 * calls the real Quipu API.
 */
@Component
@ConditionalOnProperty(prefix = "billing.integrations.quipu", name = "enabled", havingValue = "true")
public class QuipuBillingProvider implements BillingProviderPort {

    @Override
    public ProviderResult createExternalInvoice(ExternalInvoiceRequest request) {
        // TODO(future): integrate with the Quipu API. Intentionally not implemented.
        return ProviderResult.notImplemented(getProviderName());
    }

    @Override
    public String getProviderName() {
        return "QUIPU";
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
