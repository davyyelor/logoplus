package com.logopeda.paymentgateway.controller;

import com.logopeda.paymentgateway.dto.WebhookResult;
import com.logopeda.paymentgateway.service.StripePaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public webhook receiver for Stripe events. This endpoint is unauthenticated
 * (see {@code SecurityConfig}); the gateway is responsible for validating the
 * signature when running in a non-disabled mode.
 */
@RestController
public class StripeWebhookController {

    private final StripePaymentService stripePaymentService;

    public StripeWebhookController(StripePaymentService stripePaymentService) {
        this.stripePaymentService = stripePaymentService;
    }

    @PostMapping("/api/webhooks/stripe")
    public ResponseEntity<WebhookResult> receive(
            @RequestBody(required = false) String payload,
            @RequestHeader(value = "Stripe-Signature", required = false) String signature) {
        return ResponseEntity.ok(
                stripePaymentService.handleWebhook(payload == null ? "" : payload, signature));
    }
}
