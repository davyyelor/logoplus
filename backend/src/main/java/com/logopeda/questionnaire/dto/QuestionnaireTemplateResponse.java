package com.logopeda.questionnaire.dto;

import com.logopeda.questionnaire.enums.QuestionnaireTargetRole;
import java.time.Instant;
import java.util.List;

public record QuestionnaireTemplateResponse(
        String id,
        String clinicId,
        String name,
        String description,
        QuestionnaireTargetRole targetRole,
        boolean active,
        List<QuestionResponse> questions,
        Instant createdAt,
        Instant updatedAt) {
}
