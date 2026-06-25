package com.logopeda.notification.dto;

import com.logopeda.notification.enums.NotificationType;
import java.time.Instant;

public record NotificationResponse(
        String id,
        NotificationType type,
        String title,
        String message,
        String reference,
        boolean read,
        Instant createdAt) {
}
