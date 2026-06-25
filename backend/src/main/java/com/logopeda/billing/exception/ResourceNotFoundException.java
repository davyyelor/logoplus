package com.logopeda.billing.exception;

/** Thrown when a requested billing entity does not exist for the clinic. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, String id) {
        super(resource + " not found: " + id);
    }
}
