package com.logopeda.paymentgateway.controller;

import com.logopeda.paymentgateway.dto.CheckoutRequest;
import com.logopeda.paymentgateway.dto.CheckoutSessionResult;
import com.logopeda.paymentgateway.dto.ConnectAccountResult;
import com.logopeda.paymentgateway.dto.StripeStatusResponse;
import com.logopeda.paymentgateway.service.StripePaymentService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Staff-facing endpoints for the Stripe payment gateway. */
@RestController
public class StripeController {

    private final StripePaymentService stripePaymentService;

    public StripeController(StripePaymentService stripePaymentService) {
        this.stripePaymentService = stripePaymentService;
    }

    @GetMapping("/api/billing/stripe/status")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST','RECEPTION')")
    public StripeStatusResponse status() {
        return stripePaymentService.status();
    }

    @PostMapping("/api/billing/stripe/checkout")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST','RECEPTION')")
    public CheckoutSessionResult checkout(@Valid @RequestBody CheckoutRequest request) {
        return stripePaymentService.createCheckout(request);
    }

    @PostMapping("/api/billing/stripe/connect/onboard")
    @PreAuthorize("hasRole('CLINIC_ADMIN')")
    public ConnectAccountResult onboard(@RequestParam(required = false) String returnUrl) {
        return stripePaymentService.onboardConnect(returnUrl);
    }
}
