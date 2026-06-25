package com.logopeda.therapy.controller;

import com.logopeda.shared.security.TenantContext;
import com.logopeda.therapy.dto.SessionGoalProgressRequest;
import com.logopeda.therapy.dto.SessionGoalProgressResponse;
import com.logopeda.therapy.mapper.TherapyGoalMapper;
import com.logopeda.therapy.model.SessionGoalProgress;
import com.logopeda.therapy.service.SessionGoalProgressService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Goal-progress recording within a session, for clinical staff. */
@RestController
@PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST')")
public class SessionGoalProgressController {

    private final SessionGoalProgressService progressService;
    private final TherapyGoalMapper goalMapper;
    private final TenantContext tenantContext;

    public SessionGoalProgressController(SessionGoalProgressService progressService,
                                         TherapyGoalMapper goalMapper, TenantContext tenantContext) {
        this.progressService = progressService;
        this.goalMapper = goalMapper;
        this.tenantContext = tenantContext;
    }

    @GetMapping("/api/sessions/{sessionId}/goal-progress")
    public List<SessionGoalProgressResponse> listForSession(@PathVariable String sessionId) {
        String clinicId = tenantContext.requireClinicId();
        return progressService.listForSession(sessionId).stream()
                .map(p -> goalMapper.toResponse(p, progressService.goalTitle(clinicId, p.getGoalId())))
                .toList();
    }

    @PostMapping("/api/sessions/{sessionId}/goal-progress")
    @ResponseStatus(HttpStatus.CREATED)
    public SessionGoalProgressResponse create(@PathVariable String sessionId,
                                              @Valid @RequestBody SessionGoalProgressRequest request) {
        String clinicId = tenantContext.requireClinicId();
        SessionGoalProgress saved = progressService.create(sessionId, request);
        return goalMapper.toResponse(saved, progressService.goalTitle(clinicId, saved.getGoalId()));
    }

    @PutMapping("/api/session-goal-progress/{id}")
    public SessionGoalProgressResponse update(@PathVariable String id,
                                              @Valid @RequestBody SessionGoalProgressRequest request) {
        String clinicId = tenantContext.requireClinicId();
        SessionGoalProgress saved = progressService.update(id, request);
        return goalMapper.toResponse(saved, progressService.goalTitle(clinicId, saved.getGoalId()));
    }

    @DeleteMapping("/api/session-goal-progress/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        progressService.delete(id);
    }
}
