package com.logopeda.billing.integration.provider;

import com.logopeda.billing.integration.FiscalProviderPort;
import com.logopeda.billing.integration.IntegrationModels.ExternalInvoiceRequest;
import com.logopeda.billing.integration.IntegrationModels.ProviderResult;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * PLACEHOLDER for a future VeriFactu fiscal integration.
 *
 * <p>This MVP does NOT implement any VeriFactu legal/fiscal logic. Disabled by
 * default and never produces real fiscal records.
 */
@Component
@ConditionalOnProperty(prefix = "billing.integrations.verifactu", name = "enabled", havingValue = "true")
public class VeriFactuFiscalProvider implements FiscalProviderPort {

    @Override
    public ProviderResult submitFiscalRecord(ExternalInvoiceRequest request) {
        // TODO(future): implement VeriFactu compliant generation/submission. Not implemented.
        return ProviderResult.notImplemented(getProviderName());
    }

    @Override
    public String getProviderName() {
        return "VERIFACTU";
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
