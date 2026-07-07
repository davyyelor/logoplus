package com.logopeda.questionnaire.controller;

import com.logopeda.questionnaire.dto.AssignQuestionnaireRequest;
import com.logopeda.questionnaire.dto.QuestionnaireAssignmentResponse;
import com.logopeda.questionnaire.dto.QuestionnaireResponseDetail;
import com.logopeda.questionnaire.dto.QuestionnaireTemplateRequest;
import com.logopeda.questionnaire.dto.QuestionnaireTemplateResponse;
import com.logopeda.questionnaire.mapper.QuestionnaireMapper;
import com.logopeda.questionnaire.model.QuestionnaireAssignment;
import com.logopeda.questionnaire.model.QuestionnaireResponse;
import com.logopeda.questionnaire.model.QuestionnaireTemplate;
import com.logopeda.questionnaire.service.QuestionnaireService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Staff questionnaire management: templates, assignments and response review. */
@RestController
@PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST')")
public class QuestionnaireController {

    private final QuestionnaireService questionnaireService;
    private final QuestionnaireMapper questionnaireMapper;

    public QuestionnaireController(QuestionnaireService questionnaireService,
                                   QuestionnaireMapper questionnaireMapper) {
        this.questionnaireService = questionnaireService;
        this.questionnaireMapper = questionnaireMapper;
    }

    @GetMapping("/api/questionnaire-templates")
    public List<QuestionnaireTemplateResponse> listTemplates() {
        return questionnaireService.listTemplates().stream()
                .map(t -> questionnaireMapper.toResponse(t, questionnaireService.questionsOf(t.getId())))
                .toList();
    }

    @GetMapping("/api/questionnaire-templates/{id}")
    public QuestionnaireTemplateResponse getTemplate(@PathVariable String id) {
        QuestionnaireTemplate template = questionnaireService.getTemplate(id);
        return questionnaireMapper.toResponse(template, questionnaireService.questionsOf(id));
    }

    @PostMapping("/api/questionnaire-templates")
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionnaireTemplateResponse createTemplate(@Valid @RequestBody QuestionnaireTemplateRequest request) {
        QuestionnaireTemplate template = questionnaireService.createTemplate(request);
        return questionnaireMapper.toResponse(template, questionnaireService.questionsOf(template.getId()));
    }

    @PutMapping("/api/questionnaire-templates/{id}")
    public QuestionnaireTemplateResponse updateTemplate(@PathVariable String id,
                                                        @Valid @RequestBody QuestionnaireTemplateRequest request) {
        QuestionnaireTemplate template = questionnaireService.updateTemplate(id, request);
        return questionnaireMapper.toResponse(template, questionnaireService.questionsOf(template.getId()));
    }

    @PostMapping("/api/patients/{patientId}/questionnaires/assign")
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionnaireAssignmentResponse assign(@PathVariable String patientId,
                                                  @Valid @RequestBody AssignQuestionnaireRequest request) {
        QuestionnaireAssignment assignment = questionnaireService.assign(patientId, request);
        return questionnaireMapper.toResponse(assignment,
                questionnaireService.getTemplate(assignment.getTemplateId()).getName());
    }

    @GetMapping("/api/patients/{patientId}/questionnaires")
    public List<QuestionnaireAssignmentResponse> listAssignments(@PathVariable String patientId) {
        return questionnaireService.listAssignments(patientId).stream()
                .map(a -> questionnaireMapper.toResponse(a,
                        questionnaireService.getTemplate(a.getTemplateId()).getName()))
                .toList();
    }

    @PatchMapping("/api/questionnaire-assignments/{id}/cancel")
    public QuestionnaireAssignmentResponse cancel(@PathVariable String id) {
        QuestionnaireAssignment assignment = questionnaireService.cancelAssignment(id);
        return questionnaireMapper.toResponse(assignment,
                questionnaireService.getTemplate(assignment.getTemplateId()).getName());
    }

    @GetMapping("/api/questionnaire-assignments/{id}/responses")
    public List<QuestionnaireResponseDetail> listResponses(@PathVariable String id) {
        return questionnaireService.listResponses(id).stream()
                .map(this::toDetail)
                .toList();
    }

    private QuestionnaireResponseDetail toDetail(QuestionnaireResponse response) {
        return questionnaireMapper.toResponse(response, questionnaireService.answersOf(response.getId()));
    }
}
