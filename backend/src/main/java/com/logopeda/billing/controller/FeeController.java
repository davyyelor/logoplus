package com.logopeda.billing.controller;

import com.logopeda.billing.dto.FeeRequest;
import com.logopeda.billing.dto.FeeResponse;
import com.logopeda.billing.mapper.FeeMapper;
import com.logopeda.billing.service.ClinicContext;
import com.logopeda.billing.service.FeeService;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping("/api/fees")
public class FeeController {

    private final FeeService feeService;
    private final FeeMapper feeMapper;
    private final ClinicContext clinicContext;

    public FeeController(FeeService feeService, FeeMapper feeMapper, ClinicContext clinicContext) {
        this.feeService = feeService;
        this.feeMapper = feeMapper;
        this.clinicContext = clinicContext;
    }

    @GetMapping
    public List<FeeResponse> list(
            @RequestHeader(value = "X-Clinic-Id", required = false) String clinicHeader,
            @RequestParam(required = false) String patientId,
            @RequestParam(required = false) Boolean active) {
        String clinicId = clinicContext.resolveClinicId(clinicHeader);
        return feeService.search(clinicId, patientId, active)
                .stream().map(feeMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public FeeResponse get(
            @RequestHeader(value = "X-Clinic-Id", required = false) String clinicHeader,
            @PathVariable String id) {
        return feeMapper.toResponse(
                feeService.getById(clinicContext.resolveClinicId(clinicHeader), id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FeeResponse create(
            @RequestHeader(value = "X-Clinic-Id", required = false) String clinicHeader,
            @Valid @RequestBody FeeRequest request) {
        return feeMapper.toResponse(
                feeService.create(clinicContext.resolveClinicId(clinicHeader), request));
    }

    @PutMapping("/{id}")
    public FeeResponse update(
            @RequestHeader(value = "X-Clinic-Id", required = false) String clinicHeader,
            @PathVariable String id,
            @Valid @RequestBody FeeRequest request) {
        return feeMapper.toResponse(
                feeService.update(clinicContext.resolveClinicId(clinicHeader), id, request));
    }

    @PatchMapping("/{id}/activate")
    public FeeResponse activate(
            @RequestHeader(value = "X-Clinic-Id", required = false) String clinicHeader,
            @PathVariable String id) {
        return feeMapper.toResponse(
                feeService.setActive(clinicContext.resolveClinicId(clinicHeader), id, true));
    }

    @PatchMapping("/{id}/deactivate")
    public FeeResponse deactivate(
            @RequestHeader(value = "X-Clinic-Id", required = false) String clinicHeader,
            @PathVariable String id) {
        return feeMapper.toResponse(
                feeService.setActive(clinicContext.resolveClinicId(clinicHeader), id, false));
    }
}
