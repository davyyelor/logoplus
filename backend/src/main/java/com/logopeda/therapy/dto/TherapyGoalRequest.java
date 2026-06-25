package com.logopeda.therapy.dto;

import com.logopeda.therapy.enums.GoalPriority;
import com.logopeda.therapy.enums.GoalStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/** Payload to create or update a therapy goal. */
public record TherapyGoalRequest(
        String area,

        @NotBlank(message = "title is required")
        String title,

        @Size(max = 2000)
        String description,

        GoalStatus status,

        GoalPriority priority,

        LocalDate startDate,

        LocalDate targetDate,

        LocalDate achievedDate) {
}
