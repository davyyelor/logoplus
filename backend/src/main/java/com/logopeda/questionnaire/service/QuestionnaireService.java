package com.logopeda.questionnaire.service;

import com.logopeda.audit.service.AuditService;
import com.logopeda.billing.exception.BusinessValidationException;
import com.logopeda.billing.exception.ResourceNotFoundException;
import com.logopeda.guardian.service.FamilyAccessService;
import com.logopeda.patient.service.PatientAccessGuard;
import com.logopeda.questionnaire.dto.AnswerRequest;
import com.logopeda.questionnaire.dto.AssignQuestionnaireRequest;
import com.logopeda.questionnaire.dto.QuestionRequest;
import com.logopeda.questionnaire.dto.QuestionnaireTemplateRequest;
import com.logopeda.questionnaire.dto.SubmitResponseRequest;
import com.logopeda.questionnaire.enums.AssignmentStatus;
import com.logopeda.questionnaire.enums.QuestionType;
import com.logopeda.questionnaire.enums.QuestionnaireTargetRole;
import com.logopeda.questionnaire.model.QuestionnaireAnswer;
import com.logopeda.questionnaire.model.QuestionnaireAssignment;
import com.logopeda.questionnaire.model.QuestionnaireQuestion;
import com.logopeda.questionnaire.model.QuestionnaireResponse;
import com.logopeda.questionnaire.model.QuestionnaireTemplate;
import com.logopeda.questionnaire.repository.QuestionnaireAnswerRepository;
import com.logopeda.questionnaire.repository.QuestionnaireAssignmentRepository;
import com.logopeda.questionnaire.repository.QuestionnaireQuestionRepository;
import com.logopeda.questionnaire.repository.QuestionnaireResponseRepository;
import com.logopeda.questionnaire.repository.QuestionnaireTemplateRepository;
import com.logopeda.shared.enums.Role;
import com.logopeda.shared.security.AuthenticatedUser;
import com.logopeda.shared.security.TenantContext;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Parametrizable questionnaires: templates, assignments, and responses. All
 * data is clinic-scoped. Family users may only view assignments for patients
 * they are linked to whose template targets FAMILY/BOTH, and submit responses
 * to those assignments. No clinical scoring or inference is performed.
 */
@Service
@Transactional
public class QuestionnaireService {

    private final QuestionnaireTemplateRepository templateRepository;
    private final QuestionnaireQuestionRepository questionRepository;
    private final QuestionnaireAssignmentRepository assignmentRepository;
    private final QuestionnaireResponseRepository responseRepository;
    private final QuestionnaireAnswerRepository answerRepository;
    private final PatientAccessGuard patientAccessGuard;
    private final FamilyAccessService familyAccessService;
    private final TenantContext tenantContext;
    private final AuditService auditService;

    public QuestionnaireService(QuestionnaireTemplateRepository templateRepository,
                                QuestionnaireQuestionRepository questionRepository,
                                QuestionnaireAssignmentRepository assignmentRepository,
                                QuestionnaireResponseRepository responseRepository,
                                QuestionnaireAnswerRepository answerRepository,
                                PatientAccessGuard patientAccessGuard,
                                FamilyAccessService familyAccessService,
                                TenantContext tenantContext, AuditService auditService) {
        this.templateRepository = templateRepository;
        this.questionRepository = questionRepository;
        this.assignmentRepository = assignmentRepository;
        this.responseRepository = responseRepository;
        this.answerRepository = answerRepository;
        this.patientAccessGuard = patientAccessGuard;
        this.familyAccessService = familyAccessService;
        this.tenantContext = tenantContext;
        this.auditService = auditService;
    }

    // ---- Templates ----------------------------------------------------------

    @Transactional(readOnly = true)
    public List<QuestionnaireTemplate> listTemplates() {
        return templateRepository.findByClinicIdOrderByCreatedAtDesc(tenantContext.requireClinicId());
    }

    @Transactional(readOnly = true)
    public QuestionnaireTemplate getTemplate(String id) {
        return templateRepository.findByIdAndClinicId(id, tenantContext.requireClinicId())
                .orElseThrow(() -> new ResourceNotFoundException("QuestionnaireTemplate", id));
    }

    @Transactional(readOnly = true)
    public List<QuestionnaireQuestion> questionsOf(String templateId) {
        return questionRepository.findByTemplateIdAndClinicIdOrderByPositionAsc(
                templateId, tenantContext.requireClinicId());
    }

    public QuestionnaireTemplate createTemplate(QuestionnaireTemplateRequest request) {
        QuestionnaireTemplate template = new QuestionnaireTemplate();
        template.setClinicId(tenantContext.requireClinicId());
        applyTemplate(template, request);
        QuestionnaireTemplate saved = templateRepository.save(template);
        replaceQuestions(saved, request.questions());
        return saved;
    }

    public QuestionnaireTemplate updateTemplate(String id, QuestionnaireTemplateRequest request) {
        QuestionnaireTemplate template = getTemplate(id);
        applyTemplate(template, request);
        QuestionnaireTemplate saved = templateRepository.save(template);
        if (request.questions() != null) {
            questionRepository.deleteByTemplateIdAndClinicId(saved.getId(), saved.getClinicId());
            replaceQuestions(saved, request.questions());
        }
        return saved;
    }

    // ---- Assignments --------------------------------------------------------

    public QuestionnaireAssignment assign(String patientId, AssignQuestionnaireRequest request) {
        patientAccessGuard.requireAccessiblePatient(patientId);
        QuestionnaireTemplate template = getTemplate(request.templateId());
        QuestionnaireAssignment assignment = new QuestionnaireAssignment();
        assignment.setClinicId(tenantContext.requireClinicId());
        assignment.setPatientId(patientId);
        assignment.setTemplateId(template.getId());
        assignment.setAssignedByUserId(tenantContext.currentUserId());
        assignment.setAssignedToUserId(request.assignedToUserId());
        assignment.setDueDate(request.dueDate());
        assignment.setStatus(AssignmentStatus.PENDING);
        QuestionnaireAssignment saved = assignmentRepository.save(assignment);
        auditService.record("QUESTIONNAIRE_ASSIGNED", "QuestionnaireAssignment", saved.getId(), null);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<QuestionnaireAssignment> listAssignments(String patientId) {
        patientAccessGuard.requireAccessiblePatient(patientId);
        return assignmentRepository.findByPatientIdAndClinicIdOrderByCreatedAtDesc(
                patientId, tenantContext.requireClinicId());
    }

    /** Assignments visible to the current family user across all linked patients. */
    @Transactional(readOnly = true)
    public List<QuestionnaireAssignment> listFamilyAssignments() {
        AuthenticatedUser user = tenantContext.requireCurrentUser();
        String clinicId = user.getClinicId();
        List<QuestionnaireAssignment> result = new ArrayList<>();
        for (String patientId : familyAccessService.accessiblePatientIds(user.getUserId())) {
            assignmentRepository.findByPatientIdAndClinicIdOrderByCreatedAtDesc(patientId, clinicId).stream()
                    .filter(a -> isFamilyVisible(a.getTemplateId()))
                    .forEach(result::add);
        }
        return result;
    }

    public QuestionnaireAssignment cancelAssignment(String id) {
        QuestionnaireAssignment assignment = requireAssignment(id);
        assignment.setStatus(AssignmentStatus.CANCELLED);
        return assignmentRepository.save(assignment);
    }

    // ---- Responses ----------------------------------------------------------

    /**
     * Submits a response to an assignment. The current user must be able to
     * access the patient (staff by clinic, family by link). Family submissions
     * are additionally limited to templates that target FAMILY/BOTH.
     */
    public QuestionnaireResponse submitResponse(String assignmentId, SubmitResponseRequest request) {
        QuestionnaireAssignment assignment = requireAssignment(assignmentId);
        patientAccessGuard.requireAccessiblePatient(assignment.getPatientId());
        if (tenantContext.currentRole() == Role.FAMILY && !isFamilyVisible(assignment.getTemplateId())) {
            throw new ResourceNotFoundException("QuestionnaireAssignment", assignmentId);
        }
        if (assignment.getStatus() == AssignmentStatus.CANCELLED) {
            throw new BusinessValidationException("This questionnaire has been cancelled");
        }

        QuestionnaireResponse response = new QuestionnaireResponse();
        response.setClinicId(assignment.getClinicId());
        response.setAssignmentId(assignment.getId());
        response.setRespondedByUserId(tenantContext.currentUserId());
        response.setSubmittedAt(Instant.now());
        QuestionnaireResponse savedResponse = responseRepository.save(response);

        if (request.answers() != null) {
            for (AnswerRequest ans : request.answers()) {
                QuestionnaireAnswer answer = new QuestionnaireAnswer();
                answer.setClinicId(assignment.getClinicId());
                answer.setResponseId(savedResponse.getId());
                answer.setQuestionId(ans.questionId());
                answer.setAnswerText(ans.answerText());
                answer.setAnswerNumber(ans.answerNumber());
                answer.setAnswerJson(ans.answerJson());
                answerRepository.save(answer);
            }
        }

        assignment.setStatus(AssignmentStatus.COMPLETED);
        assignmentRepository.save(assignment);
        auditService.record("QUESTIONNAIRE_RESPONSE_SUBMITTED", "QuestionnaireResponse",
                savedResponse.getId(), null);
        return savedResponse;
    }

    @Transactional(readOnly = true)
    public List<QuestionnaireResponse> listResponses(String assignmentId) {
        QuestionnaireAssignment assignment = requireAssignment(assignmentId);
        patientAccessGuard.requireAccessiblePatient(assignment.getPatientId());
        return responseRepository.findByAssignmentIdAndClinicIdOrderByCreatedAtDesc(
                assignment.getId(), tenantContext.requireClinicId());
    }

    @Transactional(readOnly = true)
    public List<QuestionnaireAnswer> answersOf(String responseId) {
        return answerRepository.findByResponseIdAndClinicId(responseId, tenantContext.requireClinicId());
    }

    // ---- helpers ------------------------------------------------------------

    private boolean isFamilyVisible(String templateId) {
        return templateRepository.findByIdAndClinicId(templateId, tenantContext.requireClinicId())
                .map(t -> t.getTargetRole() == QuestionnaireTargetRole.FAMILY
                        || t.getTargetRole() == QuestionnaireTargetRole.BOTH)
                .orElse(false);
    }

    private QuestionnaireAssignment requireAssignment(String id) {
        return assignmentRepository.findByIdAndClinicId(id, tenantContext.requireClinicId())
                .orElseThrow(() -> new ResourceNotFoundException("QuestionnaireAssignment", id));
    }

    private void applyTemplate(QuestionnaireTemplate template, QuestionnaireTemplateRequest request) {
        if (request.name() == null || request.name().isBlank()) {
            throw new BusinessValidationException("Template name is required");
        }
        template.setName(request.name().trim());
        template.setDescription(request.description());
        if (request.targetRole() != null) {
            template.setTargetRole(request.targetRole());
        }
        if (request.active() != null) {
            template.setActive(request.active());
        }
    }

    private void replaceQuestions(QuestionnaireTemplate template, List<QuestionRequest> questions) {
        if (questions == null) {
            return;
        }
        int position = 0;
        for (QuestionRequest q : questions) {
            QuestionnaireQuestion question = new QuestionnaireQuestion();
            question.setClinicId(template.getClinicId());
            question.setTemplateId(template.getId());
            question.setText(q.text());
            question.setType(q.type() != null ? q.type() : QuestionType.TEXT);
            question.setOptionsJson(q.optionsJson());
            question.setRequired(Boolean.TRUE.equals(q.required()));
            question.setPosition(q.position() != null ? q.position() : position);
            questionRepository.save(question);
            position++;
        }
    }
}
