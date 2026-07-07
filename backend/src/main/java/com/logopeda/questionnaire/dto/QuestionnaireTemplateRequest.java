package com.logopeda.questionnaire.dto;

import com.logopeda.questionnaire.enums.QuestionnaireTargetRole;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record QuestionnaireTemplateRequest(
        @NotBlank String name,
        String description,
        QuestionnaireTargetRole targetRole,
        Boolean active,
        @Valid List<QuestionRequest> questions) {
}
