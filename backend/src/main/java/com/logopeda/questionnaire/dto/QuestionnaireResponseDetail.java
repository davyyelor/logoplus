package com.logopeda.questionnaire.dto;

import java.time.Instant;
import java.util.List;

public record QuestionnaireResponseDetail(
        String id,
        String assignmentId,
        String respondedByUserId,
        Instant submittedAt,
        List<AnswerResponse> answers) {
}
