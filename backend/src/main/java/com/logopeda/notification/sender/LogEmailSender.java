package com.logopeda.notification.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Placeholder email sender that only logs. Keeps V1 free of external mail
 * dependencies while letting the rest of the system call email as if it were
 * wired. Replace with a real provider implementation and mark it {@code @Primary}.
 */
@Component
@Primary
public class LogEmailSender implements EmailSenderPort {

    private static final Logger log = LoggerFactory.getLogger(LogEmailSender.class);

    @Override
    public void send(String toEmail, String subject, String body) {
        log.info("[EMAIL placeholder] to={} subject={}", toEmail, subject);
    }
}
