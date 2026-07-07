package com.logopeda.questionnaire.dto;

import com.logopeda.questionnaire.enums.QuestionType;
import jakarta.validation.constraints.NotBlank;

public record QuestionRequest(
        @NotBlank String text,
        QuestionType type,
        String optionsJson,
        Boolean required,
        Integer position) {
}
