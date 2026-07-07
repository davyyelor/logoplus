package com.logopeda.homework.dto;

import com.logopeda.homework.enums.HomeworkStatus;
import java.time.Instant;
import java.time.LocalDate;

public record HomeworkResponse(
        String id,
        String clinicId,
        String patientId,
        String sessionId,
        String createdByUserId,
        String title,
        String description,
        String instructions,
        LocalDate dueDate,
        HomeworkStatus status,
        boolean visibleToFamily,
        Instant createdAt,
        Instant updatedAt) {
}
