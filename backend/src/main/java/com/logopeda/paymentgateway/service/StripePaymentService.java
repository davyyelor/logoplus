package com.logopeda.paymentgateway.service;

import com.logopeda.audit.service.AuditService;
import com.logopeda.paymentgateway.config.StripeProperties;
import com.logopeda.paymentgateway.dto.CheckoutRequest;
import com.logopeda.paymentgateway.dto.CheckoutSessionResult;
import com.logopeda.paymentgateway.dto.ConnectAccountResult;
import com.logopeda.paymentgateway.dto.StripeStatusResponse;
import com.logopeda.paymentgateway.dto.WebhookResult;
import com.logopeda.shared.security.TenantContext;
import org.springframework.stereotype.Service;

/**
 * Application service around the Stripe payment gateway port. It adds
 * tenant-scoping and audit trails on top of whichever gateway implementation
 * is active (disabled or mock).
 */
@Service
public class StripePaymentService {

    private final StripePaymentPort gateway;
    private final StripeProperties properties;
    private final TenantContext tenantContext;
    private final AuditService auditService;

    public StripePaymentService(StripePaymentPort gateway, StripeProperties properties,
                                TenantContext tenantContext, AuditService auditService) {
        this.gateway = gateway;
        this.properties = properties;
        this.tenantContext = tenantContext;
        this.auditService = auditService;
    }

    public StripeStatusResponse status() {
        return new StripeStatusResponse(gateway.mode(), gateway.isEnabled(),
                properties.getPlatformFeePercent());
    }

    public CheckoutSessionResult createCheckout(CheckoutRequest request) {
        String clinicId = tenantContext.requireClinicId();
        CheckoutSessionResult result = gateway.createCheckoutSession(clinicId, request);
        auditService.record("STRIPE_CHECKOUT_CREATED", "StripeCheckout", result.sessionId(),
                "mode=" + result.mode());
        return result;
    }

    public ConnectAccountResult onboardConnect(String returnUrl) {
        String clinicId = tenantContext.requireClinicId();
        ConnectAccountResult result = gateway.createConnectOnboarding(clinicId, returnUrl);
        auditService.record("STRIPE_CONNECT_ONBOARD", "StripeAccount", result.accountId(),
                "mode=" + result.mode());
        return result;
    }

    public WebhookResult handleWebhook(String payload, String signatureHeader) {
        WebhookResult result = gateway.handleWebhook(payload, signatureHeader);
        if (result.handled()) {
            auditService.record("STRIPE_WEBHOOK_RECEIVED", "StripeWebhook", result.eventType(),
                    "mode=" + result.mode());
        }
        return result;
    }
}
