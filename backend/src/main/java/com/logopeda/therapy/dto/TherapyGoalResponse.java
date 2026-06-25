package com.logopeda.therapy.dto;

import com.logopeda.therapy.enums.GoalPriority;
import com.logopeda.therapy.enums.GoalStatus;
import java.time.Instant;
import java.time.LocalDate;

public record TherapyGoalResponse(
        String id,
        String clinicId,
        String patientId,
        String area,
        String title,
        String description,
        GoalStatus status,
        GoalPriority priority,
        LocalDate startDate,
        LocalDate targetDate,
        LocalDate achievedDate,
        Instant createdAt,
        Instant updatedAt) {
}
