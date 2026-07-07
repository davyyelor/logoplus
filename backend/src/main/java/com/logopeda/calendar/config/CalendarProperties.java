package com.logopeda.calendar.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration for external calendar providers. Bound from
 * {@code app.calendar.*}. Disabled by default; the .ics export works
 * regardless of the selected provider.
 */
@Component
@ConfigurationProperties(prefix = "app.calendar")
public class CalendarProperties {

    /** disabled | google | outlook */
    private String provider = "disabled";
    private String googleClientId = "";
    private String googleClientSecret = "";
    private String outlookClientId = "";
    private String outlookClientSecret = "";

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getGoogleClientId() {
        return googleClientId;
    }

    public void setGoogleClientId(String googleClientId) {
        this.googleClientId = googleClientId;
    }

    public String getGoogleClientSecret() {
        return googleClientSecret;
    }

    public void setGoogleClientSecret(String googleClientSecret) {
        this.googleClientSecret = googleClientSecret;
    }

    public String getOutlookClientId() {
        return outlookClientId;
    }

    public void setOutlookClientId(String outlookClientId) {
        this.outlookClientId = outlookClientId;
    }

    public String getOutlookClientSecret() {
        return outlookClientSecret;
    }

    public void setOutlookClientSecret(String outlookClientSecret) {
        this.outlookClientSecret = outlookClientSecret;
    }
}
