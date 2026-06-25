package com.logopeda.appointment.controller;

import com.logopeda.appointment.dto.AppointmentRequest;
import com.logopeda.appointment.dto.AppointmentResponse;
import com.logopeda.appointment.enums.AppointmentStatus;
import com.logopeda.appointment.mapper.AppointmentMapper;
import com.logopeda.appointment.service.AppointmentService;
import com.logopeda.shared.security.TenantContext;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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

/** Appointment agenda for clinic staff. */
@RestController
@RequestMapping("/api/appointments")
@PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST','RECEPTION')")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AppointmentMapper appointmentMapper;
    private final TenantContext tenantContext;

    public AppointmentController(AppointmentService appointmentService,
                                 AppointmentMapper appointmentMapper, TenantContext tenantContext) {
        this.appointmentService = appointmentService;
        this.appointmentMapper = appointmentMapper;
        this.tenantContext = tenantContext;
    }

    @GetMapping
    public List<AppointmentResponse> list(
            @RequestParam(required = false) String patientId,
            @RequestParam(required = false) String therapistId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(required = false) AppointmentStatus status) {
        String clinicId = tenantContext.requireClinicId();
        return appointmentService.search(clinicId, patientId, therapistId, from, to, status)
                .stream().map(appointmentMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public AppointmentResponse get(@PathVariable String id) {
        return appointmentMapper.toResponse(
                appointmentService.getById(tenantContext.requireClinicId(), id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse create(@Valid @RequestBody AppointmentRequest request) {
        return appointmentMapper.toResponse(
                appointmentService.create(tenantContext.requireClinicId(), request));
    }

    @PutMapping("/{id}")
    public AppointmentResponse update(@PathVariable String id,
                                      @Valid @RequestBody AppointmentRequest request) {
        return appointmentMapper.toResponse(
                appointmentService.update(tenantContext.requireClinicId(), id, request));
    }

    @PatchMapping("/{id}/cancel")
    public AppointmentResponse cancel(@PathVariable String id) {
        return appointmentMapper.toResponse(appointmentService.changeStatus(
                tenantContext.requireClinicId(), id, AppointmentStatus.CANCELLED));
    }

    @PatchMapping("/{id}/complete")
    public AppointmentResponse complete(@PathVariable String id) {
        return appointmentMapper.toResponse(appointmentService.changeStatus(
                tenantContext.requireClinicId(), id, AppointmentStatus.COMPLETED));
    }

    @PatchMapping("/{id}/no-show")
    public AppointmentResponse noShow(@PathVariable String id) {
        return appointmentMapper.toResponse(appointmentService.changeStatus(
                tenantContext.requireClinicId(), id, AppointmentStatus.NO_SHOW));
    }
}
