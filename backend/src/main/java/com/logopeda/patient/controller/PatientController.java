package com.logopeda.patient.controller;

import com.logopeda.patient.dto.PatientRequest;
import com.logopeda.patient.dto.PatientResponse;
import com.logopeda.patient.dto.UpdatePatientStatusRequest;
import com.logopeda.patient.enums.PatientStatus;
import com.logopeda.patient.mapper.PatientMapper;
import com.logopeda.patient.service.PatientAccessGuard;
import com.logopeda.patient.service.PatientService;
import com.logopeda.shared.security.TenantContext;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Patient management for clinic staff (admin, therapist, reception). */
@RestController
@RequestMapping("/api/patients")
@PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST','RECEPTION')")
public class PatientController {

    private final PatientService patientService;
    private final PatientMapper patientMapper;
    private final PatientAccessGuard patientAccessGuard;
    private final TenantContext tenantContext;

    public PatientController(PatientService patientService, PatientMapper patientMapper,
                             PatientAccessGuard patientAccessGuard, TenantContext tenantContext) {
        this.patientService = patientService;
        this.patientMapper = patientMapper;
        this.patientAccessGuard = patientAccessGuard;
        this.tenantContext = tenantContext;
    }

    @GetMapping
    public List<PatientResponse> list(
            @RequestParam(required = false) PatientStatus status,
            @RequestParam(required = false) String therapistId,
            @RequestParam(required = false) String search) {
        String clinicId = tenantContext.requireClinicId();
        return patientService.search(clinicId, status, therapistId, search)
                .stream().map(patientMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public PatientResponse get(@PathVariable String id) {
        return patientMapper.toResponse(patientAccessGuard.requireAccessiblePatient(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientResponse create(@Valid @RequestBody PatientRequest request) {
        return patientMapper.toResponse(
                patientService.create(tenantContext.requireClinicId(), request));
    }

    @PutMapping("/{id}")
    public PatientResponse update(@PathVariable String id, @Valid @RequestBody PatientRequest request) {
        return patientMapper.toResponse(
                patientService.update(tenantContext.requireClinicId(), id, request));
    }

    @PatchMapping("/{id}/status")
    public PatientResponse changeStatus(@PathVariable String id,
                                        @Valid @RequestBody UpdatePatientStatusRequest request) {
        return patientMapper.toResponse(
                patientService.changeStatus(tenantContext.requireClinicId(), id, request.status()));
    }

    /** Logical removal (sets status INACTIVE), preserving the patient history. */
    @DeleteMapping("/{id}")
    public PatientResponse delete(@PathVariable String id) {
        return patientMapper.toResponse(
                patientService.softDelete(tenantContext.requireClinicId(), id));
    }
}
