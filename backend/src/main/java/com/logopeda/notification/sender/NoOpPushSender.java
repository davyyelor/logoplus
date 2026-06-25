package com.logopeda.notification.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Placeholder push sender that does nothing but log at debug level. Lets the app
 * "send" push notifications without a provider in V1.
 */
@Component
@Primary
public class NoOpPushSender implements PushSenderPort {

    private static final Logger log = LoggerFactory.getLogger(NoOpPushSender.class);

    @Override
    public void send(String userId, String title, String body) {
        log.debug("[PUSH placeholder] user={} title={}", userId, title);
    }
}
