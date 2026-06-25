package com.logopeda.shared.security;

/** Thrown when an authenticated user attempts an action outside their scope. Mapped to 403. */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
