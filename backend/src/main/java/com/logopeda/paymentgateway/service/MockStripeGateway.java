package com.logopeda.paymentgateway.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logopeda.paymentgateway.dto.CheckoutRequest;
import com.logopeda.paymentgateway.dto.CheckoutSessionResult;
import com.logopeda.paymentgateway.dto.ConnectAccountResult;
import com.logopeda.paymentgateway.dto.WebhookResult;
import com.logopeda.paymentgateway.enums.StripeMode;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Local mock gateway for development and demos. It fabricates checkout
 * sessions, connect accounts and webhook acknowledgements without ever
 * contacting Stripe, so the whole payment flow can be exercised offline.
 */
@Component
@ConditionalOnProperty(prefix = "app.stripe", name = "mode", havingValue = "mock")
public class MockStripeGateway implements StripePaymentPort {

    private final ObjectMapper objectMapper;

    public MockStripeGateway(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public StripeMode mode() {
        return StripeMode.MOCK;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public CheckoutSessionResult createCheckoutSession(String clinicId, CheckoutRequest request) {
        String sessionId = "cs_mock_" + UUID.randomUUID().toString().replace("-", "");
        String url = "https://mock.stripe.local/checkout/" + sessionId;
        return new CheckoutSessionResult(StripeMode.MOCK, true, sessionId, url);
    }

    @Override
    public ConnectAccountResult createConnectOnboarding(String clinicId, String returnUrl) {
        String accountId = "acct_mock_" + UUID.randomUUID().toString().replace("-", "");
        String url = "https://mock.stripe.local/connect/" + accountId;
        return new ConnectAccountResult(StripeMode.MOCK, true, accountId, url);
    }

    @Override
    public WebhookResult handleWebhook(String payload, String signatureHeader) {
        String eventType = "mock.event";
        try {
            JsonNode node = objectMapper.readTree(payload);
            if (node.hasNonNull("type")) {
                eventType = node.get("type").asText();
            }
        } catch (Exception ignored) {
            // Malformed payloads are acknowledged as a generic mock event.
        }
        return new WebhookResult(StripeMode.MOCK, true, eventType);
    }
}
