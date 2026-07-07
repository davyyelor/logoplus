package com.logopeda.questionnaire.controller;

import com.logopeda.questionnaire.dto.QuestionnaireAssignmentResponse;
import com.logopeda.questionnaire.dto.QuestionnaireResponseDetail;
import com.logopeda.questionnaire.dto.QuestionnaireTemplateResponse;
import com.logopeda.questionnaire.dto.SubmitResponseRequest;
import com.logopeda.questionnaire.mapper.QuestionnaireMapper;
import com.logopeda.questionnaire.model.QuestionnaireAssignment;
import com.logopeda.questionnaire.model.QuestionnaireResponse;
import com.logopeda.questionnaire.service.QuestionnaireService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Family-facing questionnaire endpoints. Restricted to FAMILY and scoped to the
 * patients the guardian is linked to. Families see the questionnaires that target
 * families and can submit their responses.
 */
@RestController
@PreAuthorize("hasRole('FAMILY')")
public class FamilyQuestionnaireController {

    private final QuestionnaireService questionnaireService;
    private final QuestionnaireMapper questionnaireMapper;

    public FamilyQuestionnaireController(QuestionnaireService questionnaireService,
                                         QuestionnaireMapper questionnaireMapper) {
        this.questionnaireService = questionnaireService;
        this.questionnaireMapper = questionnaireMapper;
    }

    @GetMapping("/api/family/questionnaires")
    public List<QuestionnaireAssignmentResponse> myQuestionnaires() {
        return questionnaireService.listFamilyAssignments().stream()
                .map(a -> questionnaireMapper.toResponse(a,
                        questionnaireService.getTemplate(a.getTemplateId()).getName()))
                .toList();
    }

    @GetMapping("/api/family/questionnaires/{assignmentId}/template")
    public QuestionnaireTemplateResponse template(@PathVariable String assignmentId) {
        // The family submits by assignment; resolve the underlying template/questions.
        QuestionnaireAssignment assignment = resolveVisibleAssignment(assignmentId);
        return questionnaireMapper.toResponse(
                questionnaireService.getTemplate(assignment.getTemplateId()),
                questionnaireService.questionsOf(assignment.getTemplateId()));
    }

    @PostMapping("/api/family/questionnaires/{assignmentId}/responses")
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionnaireResponseDetail submit(@PathVariable String assignmentId,
                                              @Valid @RequestBody SubmitResponseRequest request) {
        QuestionnaireResponse response = questionnaireService.submitResponse(assignmentId, request);
        return questionnaireMapper.toResponse(response, questionnaireService.answersOf(response.getId()));
    }

    /** Ensures the assignment is one this family may see before exposing questions. */
    private QuestionnaireAssignment resolveVisibleAssignment(String assignmentId) {
        return questionnaireService.listFamilyAssignments().stream()
                .filter(a -> a.getId().equals(assignmentId))
                .findFirst()
                .orElseThrow(() -> new com.logopeda.billing.exception.ResourceNotFoundException(
                        "QuestionnaireAssignment", assignmentId));
    }
}
