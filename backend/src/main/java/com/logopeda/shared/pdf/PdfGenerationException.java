package com.logopeda.shared.pdf;

/** Thrown when PDF rendering fails. Mapped to 500 by the global handler. */
public class PdfGenerationException extends RuntimeException {

    public PdfGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
