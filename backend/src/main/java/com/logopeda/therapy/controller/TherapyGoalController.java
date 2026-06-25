package com.logopeda.therapy.controller;

import com.logopeda.therapy.dto.TherapyGoalRequest;
import com.logopeda.therapy.dto.TherapyGoalResponse;
import com.logopeda.therapy.dto.UpdateGoalStatusRequest;
import com.logopeda.therapy.mapper.TherapyGoalMapper;
import com.logopeda.therapy.service.TherapyGoalService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Therapeutic goal management for clinical staff. */
@RestController
@PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST')")
public class TherapyGoalController {

    private final TherapyGoalService goalService;
    private final TherapyGoalMapper goalMapper;

    public TherapyGoalController(TherapyGoalService goalService, TherapyGoalMapper goalMapper) {
        this.goalService = goalService;
        this.goalMapper = goalMapper;
    }

    @GetMapping("/api/patients/{patientId}/goals")
    public List<TherapyGoalResponse> listForPatient(@PathVariable String patientId) {
        return goalService.listForPatient(patientId)
                .stream().map(goalMapper::toResponse).toList();
    }

    @GetMapping("/api/goals/{id}")
    public TherapyGoalResponse get(@PathVariable String id) {
        return goalMapper.toResponse(goalService.getById(id));
    }

    @PostMapping("/api/patients/{patientId}/goals")
    @ResponseStatus(HttpStatus.CREATED)
    public TherapyGoalResponse create(@PathVariable String patientId,
                                      @Valid @RequestBody TherapyGoalRequest request) {
        return goalMapper.toResponse(goalService.create(patientId, request));
    }

    @PutMapping("/api/goals/{id}")
    public TherapyGoalResponse update(@PathVariable String id,
                                      @Valid @RequestBody TherapyGoalRequest request) {
        return goalMapper.toResponse(goalService.update(id, request));
    }

    @PatchMapping("/api/goals/{id}/status")
    public TherapyGoalResponse changeStatus(@PathVariable String id,
                                            @Valid @RequestBody UpdateGoalStatusRequest request) {
        return goalMapper.toResponse(goalService.changeStatus(id, request.status()));
    }

    @DeleteMapping("/api/goals/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        goalService.delete(id);
    }
}
