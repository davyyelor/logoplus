package com.logopeda.report.controller;

import com.logopeda.report.dto.ReportTemplateRequest;
import com.logopeda.report.dto.ReportTemplateResponse;
import com.logopeda.report.mapper.ReportMapper;
import com.logopeda.report.service.ReportTemplateService;
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

/** Report template management. Templates are authored by clinic staff. */
@RestController
@RequestMapping("/api/report-templates")
@PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST')")
public class ReportTemplateController {

    private final ReportTemplateService templateService;
    private final ReportMapper reportMapper;
    private final TenantContext tenantContext;

    public ReportTemplateController(ReportTemplateService templateService, ReportMapper reportMapper,
                                    TenantContext tenantContext) {
        this.templateService = templateService;
        this.reportMapper = reportMapper;
        this.tenantContext = tenantContext;
    }

    @GetMapping
    public List<ReportTemplateResponse> list() {
        return templateService.list(tenantContext.requireClinicId())
                .stream().map(reportMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ReportTemplateResponse get(@PathVariable String id) {
        return reportMapper.toResponse(templateService.getById(tenantContext.requireClinicId(), id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReportTemplateResponse create(@Valid @RequestBody ReportTemplateRequest request) {
        return reportMapper.toResponse(templateService.create(tenantContext.requireClinicId(), request));
    }

    @PutMapping("/{id}")
    public ReportTemplateResponse update(@PathVariable String id,
                                         @Valid @RequestBody ReportTemplateRequest request) {
        return reportMapper.toResponse(templateService.update(tenantContext.requireClinicId(), id, request));
    }

    @PatchMapping("/{id}/activate")
    public ReportTemplateResponse activate(@PathVariable String id) {
        return reportMapper.toResponse(templateService.setActive(tenantContext.requireClinicId(), id, true));
    }

    @PatchMapping("/{id}/deactivate")
    public ReportTemplateResponse deactivate(@PathVariable String id) {
        return reportMapper.toResponse(templateService.setActive(tenantContext.requireClinicId(), id, false));
    }
}
