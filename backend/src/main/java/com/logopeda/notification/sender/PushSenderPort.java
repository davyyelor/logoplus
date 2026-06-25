package com.logopeda.notification.sender;

/**
 * Outbound push-notification abstraction. V1 ships a no-op implementation
 * ({@link NoOpPushSender}); integrate a provider (e.g. FCM/APNs) later.
 */
public interface PushSenderPort {

    void send(String userId, String title, String body);
}
