package com.logopeda.therapy.service;

import com.logopeda.billing.exception.BusinessValidationException;
import com.logopeda.billing.exception.ResourceNotFoundException;
import com.logopeda.shared.security.TenantContext;
import com.logopeda.therapy.dto.SessionGoalProgressRequest;
import com.logopeda.therapy.model.SessionGoalProgress;
import com.logopeda.therapy.model.TherapyGoal;
import com.logopeda.therapy.model.TherapySession;
import com.logopeda.therapy.repository.SessionGoalProgressRepository;
import com.logopeda.therapy.repository.TherapyGoalRepository;
import com.logopeda.therapy.repository.TherapySessionRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Links therapy goals to sessions and records the progress observed. Validates
 * that the session and goal belong to the same clinic and patient.
 */
@Service
@Transactional
public class SessionGoalProgressService {

    private final SessionGoalProgressRepository progressRepository;
    private final TherapySessionRepository sessionRepository;
    private final TherapyGoalRepository goalRepository;
    private final TenantContext tenantContext;

    public SessionGoalProgressService(SessionGoalProgressRepository progressRepository,
                                      TherapySessionRepository sessionRepository,
                                      TherapyGoalRepository goalRepository,
                                      TenantContext tenantContext) {
        this.progressRepository = progressRepository;
        this.sessionRepository = sessionRepository;
        this.goalRepository = goalRepository;
        this.tenantContext = tenantContext;
    }

    @Transactional(readOnly = true)
    public List<SessionGoalProgress> listForSession(String sessionId) {
        String clinicId = tenantContext.requireClinicId();
        requireSession(clinicId, sessionId);
        return progressRepository.findBySessionIdAndClinicIdOrderByCreatedAtAsc(sessionId, clinicId);
    }

    public SessionGoalProgress create(String sessionId, SessionGoalProgressRequest request) {
        String clinicId = tenantContext.requireClinicId();
        TherapySession session = requireSession(clinicId, sessionId);
        TherapyGoal goal = requireGoal(clinicId, request.goalId());
        if (!goal.getPatientId().equals(session.getPatientId())) {
            throw new BusinessValidationException("Goal and session belong to different patients");
        }
        SessionGoalProgress progress = new SessionGoalProgress();
        progress.setClinicId(clinicId);
        progress.setSessionId(sessionId);
        progress.setGoalId(request.goalId());
        progress.setProgressStatus(request.progressStatus());
        progress.setNotes(request.notes());
        return progressRepository.save(progress);
    }

    public SessionGoalProgress update(String id, SessionGoalProgressRequest request) {
        String clinicId = tenantContext.requireClinicId();
        SessionGoalProgress progress = progressRepository.findByIdAndClinicId(id, clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("SessionGoalProgress", id));
        requireGoal(clinicId, request.goalId());
        progress.setGoalId(request.goalId());
        progress.setProgressStatus(request.progressStatus());
        progress.setNotes(request.notes());
        return progressRepository.save(progress);
    }

    public void delete(String id) {
        String clinicId = tenantContext.requireClinicId();
        SessionGoalProgress progress = progressRepository.findByIdAndClinicId(id, clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("SessionGoalProgress", id));
        progressRepository.delete(progress);
    }

    @Transactional(readOnly = true)
    public String goalTitle(String clinicId, String goalId) {
        return goalRepository.findByIdAndClinicId(goalId, clinicId)
                .map(TherapyGoal::getTitle)
                .orElse(null);
    }

    private TherapySession requireSession(String clinicId, String sessionId) {
        return sessionRepository.findByIdAndClinicId(sessionId, clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("TherapySession", sessionId));
    }

    private TherapyGoal requireGoal(String clinicId, String goalId) {
        return goalRepository.findByIdAndClinicId(goalId, clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("TherapyGoal", goalId));
    }
}
