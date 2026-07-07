package com.logopeda.paymentgateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration for the Stripe integration. Bound from {@code app.stripe.*}.
 * The integration is disabled by default and never contacts Stripe unless a
 * non-disabled mode plus valid keys are provided.
 */
@Component
@ConfigurationProperties(prefix = "app.stripe")
public class StripeProperties {

    /** disabled | mock */
    private String mode = "disabled";
    private String secretKey = "";
    private String webhookSecret = "";
    private double platformFeePercent = 0d;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getWebhookSecret() {
        return webhookSecret;
    }

    public void setWebhookSecret(String webhookSecret) {
        this.webhookSecret = webhookSecret;
    }

    public double getPlatformFeePercent() {
        return platformFeePercent;
    }

    public void setPlatformFeePercent(double platformFeePercent) {
        this.platformFeePercent = platformFeePercent;
    }
}
