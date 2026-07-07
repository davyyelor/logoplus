package com.logopeda.homework.service;

import com.logopeda.audit.service.AuditService;
import com.logopeda.billing.exception.BusinessValidationException;
import com.logopeda.billing.exception.ResourceNotFoundException;
import com.logopeda.homework.dto.HomeworkRequest;
import com.logopeda.homework.enums.HomeworkStatus;
import com.logopeda.homework.model.Homework;
import com.logopeda.homework.repository.HomeworkRepository;
import com.logopeda.patient.service.PatientAccessGuard;
import com.logopeda.shared.enums.Role;
import com.logopeda.shared.security.TenantContext;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Home task management. Staff (CLINIC_ADMIN/THERAPIST) manage the full lifecycle;
 * RECEPTION reads; FAMILY may only read tasks marked visible for their linked
 * patients and advance a task's status (progress/complete). All access is scoped
 * by clinic and by {@link PatientAccessGuard}.
 */
@Service
@Transactional
public class HomeworkService {

    private final HomeworkRepository homeworkRepository;
    private final PatientAccessGuard patientAccessGuard;
    private final TenantContext tenantContext;
    private final AuditService auditService;

    public HomeworkService(HomeworkRepository homeworkRepository, PatientAccessGuard patientAccessGuard,
                           TenantContext tenantContext, AuditService auditService) {
        this.homeworkRepository = homeworkRepository;
        this.patientAccessGuard = patientAccessGuard;
        this.tenantContext = tenantContext;
        this.auditService = auditService;
    }

    /** Staff / reception listing (all tasks of the patient). */
    @Transactional(readOnly = true)
    public List<Homework> listForPatient(String patientId) {
        patientAccessGuard.requireAccessiblePatient(patientId);
        return homeworkRepository.findByPatientIdAndClinicIdOrderByCreatedAtDesc(
                patientId, tenantContext.requireClinicId());
    }

    /** Family listing: only tasks explicitly visible to the family. */
    @Transactional(readOnly = true)
    public List<Homework> listForFamily(String patientId) {
        patientAccessGuard.requireAccessiblePatient(patientId);
        return homeworkRepository.findByPatientIdAndClinicIdAndVisibleToFamilyTrueOrderByCreatedAtDesc(
                patientId, tenantContext.requireClinicId());
    }

    @Transactional(readOnly = true)
    public Homework getById(String id) {
        return findOwned(id);
    }

    public Homework create(String patientId, HomeworkRequest request) {
        patientAccessGuard.requireAccessiblePatient(patientId);
        Homework homework = new Homework();
        homework.setClinicId(tenantContext.requireClinicId());
        homework.setPatientId(patientId);
        homework.setCreatedByUserId(tenantContext.currentUserId());
        homework.setStatus(HomeworkStatus.ASSIGNED);
        apply(homework, request);
        Homework saved = homeworkRepository.save(homework);
        auditService.record("HOMEWORK_CREATED", "Homework", saved.getId(), null);
        return saved;
    }

    public Homework update(String id, HomeworkRequest request) {
        Homework homework = findOwned(id);
        apply(homework, request);
        return homeworkRepository.save(homework);
    }

    /** Staff status change: any transition is allowed. */
    public Homework changeStatus(String id, HomeworkStatus status) {
        Homework homework = findOwned(id);
        homework.setStatus(status);
        return homeworkRepository.save(homework);
    }

    /**
     * Family status change: restricted to advancing progress on visible tasks of
     * linked patients. Families cannot review or cancel.
     */
    public Homework changeStatusAsFamily(String id, HomeworkStatus status) {
        if (status != HomeworkStatus.IN_PROGRESS && status != HomeworkStatus.COMPLETED) {
            throw new BusinessValidationException("Families may only mark a task as in progress or completed");
        }
        Homework homework = findOwned(id);
        if (!homework.isVisibleToFamily()) {
            throw new ResourceNotFoundException("Homework", id);
        }
        homework.setStatus(status);
        Homework saved = homeworkRepository.save(homework);
        auditService.record("HOMEWORK_STATUS_UPDATED_BY_FAMILY", "Homework", saved.getId(),
                "{\"status\":\"" + status + "\"}");
        return saved;
    }

    public void delete(String id) {
        Homework homework = findOwned(id);
        homeworkRepository.delete(homework);
    }

    private void apply(Homework homework, HomeworkRequest request) {
        if (request.title() == null || request.title().isBlank()) {
            throw new BusinessValidationException("Title is required");
        }
        homework.setTitle(request.title().trim());
        homework.setDescription(request.description());
        homework.setInstructions(request.instructions());
        homework.setDueDate(request.dueDate());
        homework.setSessionId(request.sessionId());
        if (request.visibleToFamily() != null) {
            homework.setVisibleToFamily(request.visibleToFamily());
        }
    }

    private Homework findOwned(String id) {
        Homework homework = homeworkRepository.findByIdAndClinicId(id, tenantContext.requireClinicId())
                .orElseThrow(() -> new ResourceNotFoundException("Homework", id));
        patientAccessGuard.requireAccessiblePatient(homework.getPatientId());
        if (tenantContext.currentRole() == Role.FAMILY && !homework.isVisibleToFamily()) {
            throw new ResourceNotFoundException("Homework", id);
        }
        return homework;
    }
}
