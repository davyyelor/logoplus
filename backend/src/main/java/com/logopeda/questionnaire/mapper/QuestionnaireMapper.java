package com.logopeda.questionnaire.mapper;

import com.logopeda.questionnaire.dto.AnswerResponse;
import com.logopeda.questionnaire.dto.QuestionResponse;
import com.logopeda.questionnaire.dto.QuestionnaireAssignmentResponse;
import com.logopeda.questionnaire.dto.QuestionnaireResponseDetail;
import com.logopeda.questionnaire.dto.QuestionnaireTemplateResponse;
import com.logopeda.questionnaire.model.QuestionnaireAnswer;
import com.logopeda.questionnaire.model.QuestionnaireAssignment;
import com.logopeda.questionnaire.model.QuestionnaireQuestion;
import com.logopeda.questionnaire.model.QuestionnaireResponse;
import com.logopeda.questionnaire.model.QuestionnaireTemplate;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class QuestionnaireMapper {

    public QuestionResponse toResponse(QuestionnaireQuestion q) {
        return new QuestionResponse(q.getId(), q.getText(), q.getType(),
                q.getOptionsJson(), q.isRequired(), q.getPosition());
    }

    public QuestionnaireTemplateResponse toResponse(QuestionnaireTemplate template,
                                                    List<QuestionnaireQuestion> questions) {
        return new QuestionnaireTemplateResponse(
                template.getId(),
                template.getClinicId(),
                template.getName(),
                template.getDescription(),
                template.getTargetRole(),
                template.isActive(),
                questions.stream().map(this::toResponse).toList(),
                template.getCreatedAt(),
                template.getUpdatedAt());
    }

    public QuestionnaireAssignmentResponse toResponse(QuestionnaireAssignment a, String templateName) {
        return new QuestionnaireAssignmentResponse(
                a.getId(),
                a.getClinicId(),
                a.getPatientId(),
                a.getTemplateId(),
                templateName,
                a.getAssignedByUserId(),
                a.getAssignedToUserId(),
                a.getDueDate(),
                a.getStatus(),
                a.getCreatedAt());
    }

    public AnswerResponse toResponse(QuestionnaireAnswer answer) {
        return new AnswerResponse(answer.getId(), answer.getQuestionId(),
                answer.getAnswerText(), answer.getAnswerNumber(), answer.getAnswerJson());
    }

    public QuestionnaireResponseDetail toResponse(QuestionnaireResponse response,
                                                  List<QuestionnaireAnswer> answers) {
        return new QuestionnaireResponseDetail(
                response.getId(),
                response.getAssignmentId(),
                response.getRespondedByUserId(),
                response.getSubmittedAt(),
                answers.stream().map(this::toResponse).toList());
    }
}
