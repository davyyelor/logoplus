package com.logopeda.therapy.dto;

import com.logopeda.therapy.enums.GoalStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateGoalStatusRequest(
        @NotNull(message = "status is required")
        GoalStatus status) {
}
