package com.logopeda.shared.security;

/** Thrown when a request lacks a valid authenticated principal. Mapped to 401. */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
