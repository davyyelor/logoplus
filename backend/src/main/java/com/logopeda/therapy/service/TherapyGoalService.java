package com.logopeda.therapy.service;

import com.logopeda.billing.exception.ResourceNotFoundException;
import com.logopeda.patient.service.PatientAccessGuard;
import com.logopeda.shared.security.TenantContext;
import com.logopeda.therapy.dto.TherapyGoalRequest;
import com.logopeda.therapy.enums.GoalPriority;
import com.logopeda.therapy.enums.GoalStatus;
import com.logopeda.therapy.model.TherapyGoal;
import com.logopeda.therapy.repository.TherapyGoalRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Therapeutic goal management, scoped to the clinic and the patient. */
@Service
@Transactional
public class TherapyGoalService {

    private final TherapyGoalRepository goalRepository;
    private final PatientAccessGuard patientAccessGuard;
    private final TenantContext tenantContext;

    public TherapyGoalService(TherapyGoalRepository goalRepository,
                              PatientAccessGuard patientAccessGuard, TenantContext tenantContext) {
        this.goalRepository = goalRepository;
        this.patientAccessGuard = patientAccessGuard;
        this.tenantContext = tenantContext;
    }

    @Transactional(readOnly = true)
    public List<TherapyGoal> listForPatient(String patientId) {
        patientAccessGuard.requireAccessiblePatient(patientId);
        return goalRepository.findByPatientIdAndClinicIdOrderByCreatedAtDesc(
                patientId, tenantContext.requireClinicId());
    }

    @Transactional(readOnly = true)
    public TherapyGoal getById(String id) {
        return findOwned(id);
    }

    public TherapyGoal create(String patientId, TherapyGoalRequest request) {
        patientAccessGuard.requireAccessiblePatient(patientId);
        TherapyGoal goal = new TherapyGoal();
        goal.setClinicId(tenantContext.requireClinicId());
        goal.setPatientId(patientId);
        goal.setStatus(request.status() != null ? request.status() : GoalStatus.NOT_STARTED);
        goal.setPriority(request.priority() != null ? request.priority() : GoalPriority.MEDIUM);
        apply(goal, request);
        return goalRepository.save(goal);
    }

    public TherapyGoal update(String id, TherapyGoalRequest request) {
        TherapyGoal goal = findOwned(id);
        if (request.status() != null) {
            goal.setStatus(request.status());
        }
        if (request.priority() != null) {
            goal.setPriority(request.priority());
        }
        apply(goal, request);
        return goalRepository.save(goal);
    }

    public TherapyGoal changeStatus(String id, GoalStatus status) {
        TherapyGoal goal = findOwned(id);
        goal.setStatus(status);
        if (status == GoalStatus.ACHIEVED && goal.getAchievedDate() == null) {
            goal.setAchievedDate(LocalDate.now());
        }
        return goalRepository.save(goal);
    }

    public void delete(String id) {
        TherapyGoal goal = findOwned(id);
        goalRepository.delete(goal);
    }

    private void apply(TherapyGoal goal, TherapyGoalRequest request) {
        goal.setArea(request.area());
        goal.setTitle(request.title().trim());
        goal.setDescription(request.description());
        goal.setStartDate(request.startDate());
        goal.setTargetDate(request.targetDate());
        goal.setAchievedDate(request.achievedDate());
    }

    private TherapyGoal findOwned(String id) {
        TherapyGoal goal = goalRepository.findByIdAndClinicId(id, tenantContext.requireClinicId())
                .orElseThrow(() -> new ResourceNotFoundException("TherapyGoal", id));
        patientAccessGuard.requireAccessiblePatient(goal.getPatientId());
        return goal;
    }
}
