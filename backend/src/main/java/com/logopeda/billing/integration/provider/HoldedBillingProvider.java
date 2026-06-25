package com.logopeda.billing.integration.provider;

import com.logopeda.billing.integration.BillingProviderPort;
import com.logopeda.billing.integration.IntegrationModels.ExternalInvoiceRequest;
import com.logopeda.billing.integration.IntegrationModels.ProviderResult;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * PLACEHOLDER for a future Holded integration.
 *
 * <p>Disabled by default ({@code billing.integrations.holded.enabled=false}).
 * It never calls the real Holded API. When disabled it returns a clear
 * "not implemented" result so callers can degrade gracefully.
 */
@Component
@ConditionalOnProperty(prefix = "billing.integrations.holded", name = "enabled", havingValue = "true")
public class HoldedBillingProvider implements BillingProviderPort {

    @Override
    public ProviderResult createExternalInvoice(ExternalInvoiceRequest request) {
        // TODO(future): integrate with the Holded API. Intentionally not implemented.
        return ProviderResult.notImplemented(getProviderName());
    }

    @Override
    public String getProviderName() {
        return "HOLDED";
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
