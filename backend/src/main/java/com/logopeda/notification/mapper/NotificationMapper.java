package com.logopeda.notification.mapper;

import com.logopeda.notification.dto.NotificationResponse;
import com.logopeda.notification.model.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getReference(),
                notification.isReadFlag(),
                notification.getCreatedAt());
    }
}
