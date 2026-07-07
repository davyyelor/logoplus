package com.logopeda.questionnaire.dto;

import jakarta.validation.Valid;
import java.util.List;

public record SubmitResponseRequest(
        @Valid List<AnswerRequest> answers) {
}
