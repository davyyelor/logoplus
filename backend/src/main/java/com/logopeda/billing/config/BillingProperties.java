package com.logopeda.billing.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Centralizes billing module configuration so values are not hardcoded across
 * the codebase. Bound from the {@code billing.*} section of application.yml.
 */
@ConfigurationProperties(prefix = "billing")
public class BillingProperties {

    private String defaultClinicId = "clinic-default";
    private String defaultCurrency = "EUR";
    private boolean seedSampleData = true;

    public String getDefaultClinicId() {
        return defaultClinicId;
    }

    public void setDefaultClinicId(String defaultClinicId) {
        this.defaultClinicId = defaultClinicId;
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }

    public boolean isSeedSampleData() {
        return seedSampleData;
    }

    public void setSeedSampleData(boolean seedSampleData) {
        this.seedSampleData = seedSampleData;
    }
}
