package com.logopeda.billing.service;

import com.logopeda.billing.config.BillingProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Resolves the current clinic (tenant) and default currency. In this MVP there
 * is no authentication yet, so the clinic id is taken from the {@code X-Clinic-Id}
 * header and falls back to the configured default. This single seam makes it
 * easy to swap in a real auth-derived tenant later without touching services.
 */
@Component
public class ClinicContext {

    private final BillingProperties properties;

    public ClinicContext(BillingProperties properties) {
        this.properties = properties;
    }

    public String resolveClinicId(String headerClinicId) {
        return StringUtils.hasText(headerClinicId) ? headerClinicId : properties.getDefaultClinicId();
    }

    public String resolveCurrency(String requestedCurrency) {
        return StringUtils.hasText(requestedCurrency)
                ? requestedCurrency.toUpperCase()
                : properties.getDefaultCurrency();
    }

    public String defaultCurrency() {
        return properties.getDefaultCurrency();
    }
}
