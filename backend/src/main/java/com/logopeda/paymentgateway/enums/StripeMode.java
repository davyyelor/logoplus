package com.logopeda.paymentgateway.enums;

/** Operating mode for the Stripe payment gateway integration. */
public enum StripeMode {
    /** No external calls; create operations are rejected. */
    DISABLED,
    /** Local mock that returns fake sessions/accounts without external calls. */
    MOCK
}
