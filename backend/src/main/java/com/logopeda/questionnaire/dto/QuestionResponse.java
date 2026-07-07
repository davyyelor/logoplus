package com.logopeda.questionnaire.dto;

import com.logopeda.questionnaire.enums.QuestionType;

public record QuestionResponse(
        String id,
        String text,
        QuestionType type,
        String optionsJson,
        boolean required,
        int position) {
}
