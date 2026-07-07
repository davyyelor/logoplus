package com.logopeda.signature.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Binds {@code app.signature.*} configuration. The provider defaults to
 * {@code internal-basic} so that the built-in electronic signature works out of
 * the box; setting it to {@code advanced-disabled} activates the disabled
 * placeholder for an external qualified-signature integration.
 */
@Component
@ConfigurationProperties(prefix = "app.signature")
public class SignatureProperties {

    private String provider = "internal-basic";

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
