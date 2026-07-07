package com.logopeda.questionnaire.dto;

public record AnswerResponse(
        String id,
        String questionId,
        String answerText,
        Double answerNumber,
        String answerJson) {
}
