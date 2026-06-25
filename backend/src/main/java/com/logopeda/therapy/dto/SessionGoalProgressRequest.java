package com.logopeda.therapy.dto;

import com.logopeda.therapy.enums.GoalProgressStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Payload to record/update progress of a goal within a session. */
public record SessionGoalProgressRequest(
        @NotBlank(message = "goalId is required")
        String goalId,

        @NotNull(message = "progressStatus is required")
        GoalProgressStatus progressStatus,

        @Size(max = 2000)
        String notes) {
}
