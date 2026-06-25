package com.logopeda.therapy.controller;

import com.logopeda.shared.security.TenantContext;
import com.logopeda.therapy.dto.TherapySessionRequest;
import com.logopeda.therapy.dto.TherapySessionResponse;
import com.logopeda.therapy.enums.SessionType;
import com.logopeda.therapy.mapper.TherapySessionMapper;
import com.logopeda.therapy.service.TherapySessionService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Therapy session records. Restricted to clinical staff (admin, therapist). */
@RestController
@RequestMapping("/api/sessions")
@PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST')")
public class TherapySessionController {

    private final TherapySessionService sessionService;
    private final TherapySessionMapper sessionMapper;
    private final TenantContext tenantContext;

    public TherapySessionController(TherapySessionService sessionService,
                                    TherapySessionMapper sessionMapper, TenantContext tenantContext) {
        this.sessionService = sessionService;
        this.sessionMapper = sessionMapper;
        this.tenantContext = tenantContext;
    }

    @GetMapping
    public List<TherapySessionResponse> list(
            @RequestParam(required = false) String patientId,
            @RequestParam(required = false) String therapistId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) SessionType sessionType) {
        String clinicId = tenantContext.requireClinicId();
        return sessionService.search(clinicId, patientId, therapistId, fromDate, toDate, sessionType)
                .stream().map(sessionMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public TherapySessionResponse get(@PathVariable String id) {
        return sessionMapper.toResponse(sessionService.getById(tenantContext.requireClinicId(), id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TherapySessionResponse create(@Valid @RequestBody TherapySessionRequest request) {
        return sessionMapper.toResponse(
                sessionService.create(tenantContext.requireClinicId(), request));
    }

    @PutMapping("/{id}")
    public TherapySessionResponse update(@PathVariable String id,
                                         @Valid @RequestBody TherapySessionRequest request) {
        return sessionMapper.toResponse(
                sessionService.update(tenantContext.requireClinicId(), id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        sessionService.delete(tenantContext.requireClinicId(), id);
    }
}
