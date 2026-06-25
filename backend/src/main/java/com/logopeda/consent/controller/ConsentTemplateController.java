package com.logopeda.consent.controller;

import com.logopeda.consent.dto.ConsentTemplateRequest;
import com.logopeda.consent.dto.ConsentTemplateResponse;
import com.logopeda.consent.mapper.ConsentMapper;
import com.logopeda.consent.service.ConsentTemplateService;
import com.logopeda.shared.security.TenantContext;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Consent template management for clinic staff. */
@RestController
@RequestMapping("/api/consent-templates")
@PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST')")
public class ConsentTemplateController {

    private final ConsentTemplateService templateService;
    private final ConsentMapper consentMapper;
    private final TenantContext tenantContext;

    public ConsentTemplateController(ConsentTemplateService templateService, ConsentMapper consentMapper,
                                     TenantContext tenantContext) {
        this.templateService = templateService;
        this.consentMapper = consentMapper;
        this.tenantContext = tenantContext;
    }

    @GetMapping
    public List<ConsentTemplateResponse> list() {
        return templateService.list(tenantContext.requireClinicId())
                .stream().map(consentMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ConsentTemplateResponse get(@PathVariable String id) {
        return consentMapper.toResponse(templateService.getById(tenantContext.requireClinicId(), id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConsentTemplateResponse create(@Valid @RequestBody ConsentTemplateRequest request) {
        return consentMapper.toResponse(templateService.create(tenantContext.requireClinicId(), request));
    }

    @PutMapping("/{id}")
    public ConsentTemplateResponse update(@PathVariable String id,
                                          @Valid @RequestBody ConsentTemplateRequest request) {
        return consentMapper.toResponse(templateService.update(tenantContext.requireClinicId(), id, request));
    }

    @PatchMapping("/{id}/activate")
    public ConsentTemplateResponse activate(@PathVariable String id) {
        return consentMapper.toResponse(templateService.setActive(tenantContext.requireClinicId(), id, true));
    }

    @PatchMapping("/{id}/deactivate")
    public ConsentTemplateResponse deactivate(@PathVariable String id) {
        return consentMapper.toResponse(templateService.setActive(tenantContext.requireClinicId(), id, false));
    }
}
