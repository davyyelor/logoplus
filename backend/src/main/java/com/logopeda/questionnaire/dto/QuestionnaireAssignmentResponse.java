package com.logopeda.questionnaire.dto;

import com.logopeda.questionnaire.enums.AssignmentStatus;
import java.time.Instant;
import java.time.LocalDate;

public record QuestionnaireAssignmentResponse(
        String id,
        String clinicId,
        String patientId,
        String templateId,
        String templateName,
        String assignedByUserId,
        String assignedToUserId,
        LocalDate dueDate,
        AssignmentStatus status,
        Instant createdAt) {
}
