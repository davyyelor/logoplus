package com.logopeda.billing.exception;

/** Thrown when a business rule or validation invariant is violated. */
public class BusinessValidationException extends RuntimeException {

    public BusinessValidationException(String message) {
        super(message);
    }
}
