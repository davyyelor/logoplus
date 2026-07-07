package com.logopeda.questionnaire.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record AssignQuestionnaireRequest(
        @NotBlank String templateId,
        String assignedToUserId,
        LocalDate dueDate) {
}
