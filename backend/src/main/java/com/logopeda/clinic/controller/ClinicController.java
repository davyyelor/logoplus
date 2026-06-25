package com.logopeda.clinic.controller;

import com.logopeda.clinic.dto.ClinicRequest;
import com.logopeda.clinic.dto.ClinicResponse;
import com.logopeda.clinic.mapper.ClinicMapper;
import com.logopeda.clinic.service.ClinicService;
import com.logopeda.shared.security.TenantContext;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Clinic settings for the caller's own clinic. */
@RestController
@RequestMapping("/api/clinics")
public class ClinicController {

    private final ClinicService clinicService;
    private final ClinicMapper clinicMapper;
    private final TenantContext tenantContext;

    public ClinicController(ClinicService clinicService, ClinicMapper clinicMapper,
                            TenantContext tenantContext) {
        this.clinicService = clinicService;
        this.clinicMapper = clinicMapper;
        this.tenantContext = tenantContext;
    }

    @GetMapping("/current")
    public ClinicResponse current() {
        return clinicMapper.toResponse(clinicService.getById(tenantContext.requireClinicId()));
    }

    @PutMapping("/current")
    @PreAuthorize("hasRole('CLINIC_ADMIN')")
    public ClinicResponse updateCurrent(@Valid @RequestBody ClinicRequest request) {
        return clinicMapper.toResponse(
                clinicService.update(tenantContext.requireClinicId(), request));
    }
}
