package com.logopeda.questionnaire.dto;

import jakarta.validation.constraints.NotBlank;

public record AnswerRequest(
        @NotBlank String questionId,
        String answerText,
        Double answerNumber,
        String answerJson) {
}
