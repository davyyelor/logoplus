package com.logopeda.billing.controller;

import com.logopeda.billing.dto.SessionBillingRequest;
import com.logopeda.billing.dto.SessionBillingResponse;
import com.logopeda.billing.enums.SessionBillingStatus;
import com.logopeda.billing.mapper.SessionBillingMapper;
import com.logopeda.billing.service.ClinicContext;
import com.logopeda.billing.service.SessionBillingService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/session-billing")
public class SessionBillingController {

    private final SessionBillingService sessionBillingService;
    private final SessionBillingMapper sessionBillingMapper;
    private final ClinicContext clinicContext;

    public SessionBillingController(SessionBillingService sessionBillingService,
                                    SessionBillingMapper sessionBillingMapper,
                                    ClinicContext clinicContext) {
        this.sessionBillingService = sessionBillingService;
        this.sessionBillingMapper = sessionBillingMapper;
        this.clinicContext = clinicContext;
    }

    @GetMapping
    public List<SessionBillingResponse> list(
            @RequestHeader(value = "X-Clinic-Id", required = false) String clinicHeader,
            @RequestParam(required = false) String patientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) SessionBillingStatus status) {
        String clinicId = clinicContext.resolveClinicId(clinicHeader);
        return sessionBillingService.search(clinicId, patientId, fromDate, toDate, status)
                .stream().map(sessionBillingMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public SessionBillingResponse get(
            @RequestHeader(value = "X-Clinic-Id", required = false) String clinicHeader,
            @PathVariable String id) {
        return sessionBillingMapper.toResponse(
                sessionBillingService.getById(clinicContext.resolveClinicId(clinicHeader), id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SessionBillingResponse create(
            @RequestHeader(value = "X-Clinic-Id", required = false) String clinicHeader,
            @Valid @RequestBody SessionBillingRequest request) {
        return sessionBillingMapper.toResponse(
                sessionBillingService.create(clinicContext.resolveClinicId(clinicHeader), request));
    }

    @PutMapping("/{id}")
    public SessionBillingResponse update(
            @RequestHeader(value = "X-Clinic-Id", required = false) String clinicHeader,
            @PathVariable String id,
            @Valid @RequestBody SessionBillingRequest request) {
        return sessionBillingMapper.toResponse(
                sessionBillingService.update(clinicContext.resolveClinicId(clinicHeader), id, request));
    }

    @PatchMapping("/{id}/mark-paid")
    public SessionBillingResponse markPaid(
            @RequestHeader(value = "X-Clinic-Id", required = false) String clinicHeader,
            @PathVariable String id) {
        return sessionBillingMapper.toResponse(
                sessionBillingService.markPaid(clinicContext.resolveClinicId(clinicHeader), id));
    }

    @PatchMapping("/{id}/mark-pending")
    public SessionBillingResponse markPending(
            @RequestHeader(value = "X-Clinic-Id", required = false) String clinicHeader,
            @PathVariable String id) {
        return sessionBillingMapper.toResponse(
                sessionBillingService.markPending(clinicContext.resolveClinicId(clinicHeader), id));
    }

    @PatchMapping("/{id}/mark-no-charge")
    public SessionBillingResponse markNoCharge(
            @RequestHeader(value = "X-Clinic-Id", required = false) String clinicHeader,
            @PathVariable String id) {
        return sessionBillingMapper.toResponse(
                sessionBillingService.markNoCharge(clinicContext.resolveClinicId(clinicHeader), id));
    }
}
