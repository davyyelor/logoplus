package com.logopeda.billing.controller;

import com.logopeda.billing.dto.PaymentRequest;
import com.logopeda.billing.dto.PaymentResponse;
import com.logopeda.billing.enums.PaymentMethod;
import com.logopeda.billing.enums.PaymentStatus;
import com.logopeda.billing.mapper.PaymentMapper;
import com.logopeda.billing.service.ClinicContext;
import com.logopeda.billing.service.PaymentService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;
    private final ClinicContext clinicContext;

    public PaymentController(PaymentService paymentService, PaymentMapper paymentMapper,
                             ClinicContext clinicContext) {
        this.paymentService = paymentService;
        this.paymentMapper = paymentMapper;
        this.clinicContext = clinicContext;
    }

    @GetMapping
    public List<PaymentResponse> list(
            @RequestHeader(value = "X-Clinic-Id", required = false) String clinicHeader,
            @RequestParam(required = false) String patientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) PaymentMethod method,
            @RequestParam(required = false) PaymentStatus status) {
        String clinicId = clinicContext.resolveClinicId(clinicHeader);
        return paymentService.search(clinicId, patientId, fromDate, toDate, method, status)
                .stream().map(paymentMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public PaymentResponse get(
            @RequestHeader(value = "X-Clinic-Id", required = false) String clinicHeader,
            @PathVariable String id) {
        return paymentMapper.toResponse(
                paymentService.getById(clinicContext.resolveClinicId(clinicHeader), id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse create(
            @RequestHeader(value = "X-Clinic-Id", required = false) String clinicHeader,
            @Valid @RequestBody PaymentRequest request) {
        return paymentMapper.toResponse(
                paymentService.create(clinicContext.resolveClinicId(clinicHeader), request));
    }

    @PutMapping("/{id}")
    public PaymentResponse update(
            @RequestHeader(value = "X-Clinic-Id", required = false) String clinicHeader,
            @PathVariable String id,
            @Valid @RequestBody PaymentRequest request) {
        return paymentMapper.toResponse(
                paymentService.update(clinicContext.resolveClinicId(clinicHeader), id, request));
    }

    /** Logical cancellation: keeps the record for audit instead of hard delete. */
    @DeleteMapping("/{id}")
    public PaymentResponse cancel(
            @RequestHeader(value = "X-Clinic-Id", required = false) String clinicHeader,
            @PathVariable String id) {
        return paymentMapper.toResponse(
                paymentService.cancel(clinicContext.resolveClinicId(clinicHeader), id));
    }

    @PostMapping("/{id}/refund")
    public PaymentResponse refund(
            @RequestHeader(value = "X-Clinic-Id", required = false) String clinicHeader,
            @PathVariable String id) {
        return paymentMapper.toResponse(
                paymentService.refund(clinicContext.resolveClinicId(clinicHeader), id));
    }
}
