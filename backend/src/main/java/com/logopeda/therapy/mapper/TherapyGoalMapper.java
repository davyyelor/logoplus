package com.logopeda.therapy.mapper;

import com.logopeda.therapy.dto.SessionGoalProgressResponse;
import com.logopeda.therapy.dto.TherapyGoalResponse;
import com.logopeda.therapy.model.SessionGoalProgress;
import com.logopeda.therapy.model.TherapyGoal;
import org.springframework.stereotype.Component;

@Component
public class TherapyGoalMapper {

    public TherapyGoalResponse toResponse(TherapyGoal goal) {
        return new TherapyGoalResponse(
                goal.getId(),
                goal.getClinicId(),
                goal.getPatientId(),
                goal.getArea(),
                goal.getTitle(),
                goal.getDescription(),
                goal.getStatus(),
                goal.getPriority(),
                goal.getStartDate(),
                goal.getTargetDate(),
                goal.getAchievedDate(),
                goal.getCreatedAt(),
                goal.getUpdatedAt());
    }

    public SessionGoalProgressResponse toResponse(SessionGoalProgress progress, String goalTitle) {
        return new SessionGoalProgressResponse(
                progress.getId(),
                progress.getClinicId(),
                progress.getSessionId(),
                progress.getGoalId(),
                goalTitle,
                progress.getProgressStatus(),
                progress.getNotes(),
                progress.getCreatedAt(),
                progress.getUpdatedAt());
    }
}
