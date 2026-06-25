package com.logopeda.therapy.dto;

import com.logopeda.therapy.enums.GoalProgressStatus;
import java.time.Instant;

public record SessionGoalProgressResponse(
        String id,
        String clinicId,
        String sessionId,
        String goalId,
        String goalTitle,
        GoalProgressStatus progressStatus,
        String notes,
        Instant createdAt,
        Instant updatedAt) {
}
