package com.logopeda.notification.sender;

/**
 * Outbound email abstraction. V1 ships only a logging implementation
 * ({@link LogEmailSender}); a real SMTP/provider implementation can be added
 * later without touching callers.
 */
public interface EmailSenderPort {

    void send(String toEmail, String subject, String body);
}
