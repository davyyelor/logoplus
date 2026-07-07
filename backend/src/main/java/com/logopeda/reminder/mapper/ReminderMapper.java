package com.logopeda.reminder.mapper;

import com.logopeda.reminder.dto.ReminderResponse;
import com.logopeda.reminder.model.Reminder;
import org.springframework.stereotype.Component;

@Component
public class ReminderMapper {

    public ReminderResponse toResponse(Reminder reminder) {
        return new ReminderResponse(
                reminder.getId(),
                reminder.getClinicId(),
                reminder.getPatientId(),
                reminder.getTargetUserId(),
                reminder.getCreatedByUserId(),
                reminder.getTitle(),
                reminder.getMessage(),
                reminder.getRemindAt(),
                reminder.getStatus(),
                reminder.getRelatedEntityType(),
                reminder.getRelatedEntityId(),
                reminder.getSentAt(),
                reminder.getCreatedAt(),
                reminder.getUpdatedAt());
    }
}
