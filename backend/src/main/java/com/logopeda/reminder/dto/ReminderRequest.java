package com.logopeda.reminder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record ReminderRequest(
        String patientId,
        String targetUserId,
        @NotBlank String title,
        String message,
        @NotNull Instant remindAt,
        String relatedEntityType,
        String relatedEntityId) {
}
