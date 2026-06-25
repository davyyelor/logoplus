package com.logopeda.shared.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Application-wide configuration bound from the {@code app.*} section of
 * application.yml. Keeps secrets, storage and seed flags out of the code.
 */
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final Security security = new Security();
    private final Storage storage = new Storage();
    private boolean seedDemoData = true;

    public Security getSecurity() {
        return security;
    }

    public Storage getStorage() {
        return storage;
    }

    public boolean isSeedDemoData() {
        return seedDemoData;
    }

    public void setSeedDemoData(boolean seedDemoData) {
        this.seedDemoData = seedDemoData;
    }

    public static class Security {
        private final Jwt jwt = new Jwt();

        public Jwt getJwt() {
            return jwt;
        }

        public static class Jwt {
            private String secret = "local-dev-secret-change-me-please-32bytes-min!!";
            private long expirationMinutes = 480;

            public String getSecret() {
                return secret;
            }

            public void setSecret(String secret) {
                this.secret = secret;
            }

            public long getExpirationMinutes() {
                return expirationMinutes;
            }

            public void setExpirationMinutes(long expirationMinutes) {
                this.expirationMinutes = expirationMinutes;
            }
        }
    }

    public static class Storage {
        private String localRoot = "./data/uploads";
        private long maxFileSizeBytes = 26_214_400L;
        private String allowedContentTypes =
                "application/pdf,image/png,image/jpeg,image/gif,image/webp";

        public String getLocalRoot() {
            return localRoot;
        }

        public void setLocalRoot(String localRoot) {
            this.localRoot = localRoot;
        }

        public long getMaxFileSizeBytes() {
            return maxFileSizeBytes;
        }

        public void setMaxFileSizeBytes(long maxFileSizeBytes) {
            this.maxFileSizeBytes = maxFileSizeBytes;
        }

        public String getAllowedContentTypes() {
            return allowedContentTypes;
        }

        public void setAllowedContentTypes(String allowedContentTypes) {
            this.allowedContentTypes = allowedContentTypes;
        }
    }
}
