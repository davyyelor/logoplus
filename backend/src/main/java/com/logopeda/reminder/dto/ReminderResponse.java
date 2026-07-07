package com.logopeda.reminder.dto;

import com.logopeda.reminder.enums.ReminderStatus;
import java.time.Instant;

public record ReminderResponse(
        String id,
        String clinicId,
        String patientId,
        String targetUserId,
        String createdByUserId,
        String title,
        String message,
        Instant remindAt,
        ReminderStatus status,
        String relatedEntityType,
        String relatedEntityId,
        Instant sentAt,
        Instant createdAt,
        Instant updatedAt) {
}
