package com.logopeda.appointment.controller;

import com.logopeda.appointment.dto.AppointmentRequest;
import com.logopeda.appointment.dto.AppointmentResponse;
import com.logopeda.appointment.enums.AppointmentStatus;
import com.logopeda.appointment.mapper.AppointmentMapper;
import com.logopeda.appointment.service.AppointmentService;
import com.logopeda.shared.security.TenantContext;
import jakarta.validation.Valid;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(AppointmentController.class);

    private final AppointmentService appointmentService;
    private final AppointmentMapper appointmentMapper;
    private final TenantContext tenantContext;

    public AppointmentController(AppointmentService appointmentService,
                                 AppointmentMapper appointmentMapper,
                                 TenantContext tenantContext) {
        this.appointmentService = appointmentService;
        this.appointmentMapper = appointmentMapper;
        this.tenantContext = tenantContext;
    }

    @GetMapping
    public List<AppointmentResponse> list(
            @RequestParam(required = false) String patientId,
            @RequestParam(required = false) String therapistId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) AppointmentStatus status) {

        String traceId = UUID.randomUUID().toString();

        log.info(
                "[{}] START GET /api/appointments patientId={} therapistId={} from={} to={} status={}",
                traceId, patientId, therapistId, from, to, status
        );

        try {
            String clinicId = tenantContext.requireClinicId();

            log.debug("[{}] Resolved clinicId={}", traceId, clinicId);

            ZoneId zone = ZoneId.systemDefault();

            Instant fromInstant = from != null
                    ? from.atStartOfDay(zone).toInstant()
                    : null;

            Instant toExclusive = to != null
                    ? to.plusDays(1).atStartOfDay(zone).toInstant()
                    : null;

            log.debug(
                    "[{}] Converted date filters to instants. zone={} fromInstant={} toExclusive={}",
                    traceId, zone, fromInstant, toExclusive
            );

            List<AppointmentResponse> response = appointmentService
                    .search(clinicId, patientId, therapistId, fromInstant, toExclusive, status)
                    .stream()
                    .map(appointmentMapper::toResponse)
                    .toList();

            log.info(
                    "[{}] SUCCESS GET /api/appointments resultCount={}",
                    traceId, response.size()
            );

            return response;

        } catch (Exception ex) {
            log.error(
                    "[{}] ERROR GET /api/appointments patientId={} therapistId={} from={} to={} status={} message={}",
                    traceId, patientId, therapistId, from, to, status, ex.getMessage(), ex
            );
            throw ex;
        }
    }

    @GetMapping("/{id}")
    public AppointmentResponse get(@PathVariable String id) {
        String traceId = UUID.randomUUID().toString();

        log.info("[{}] START GET /api/appointments/{}", traceId, id);

        try {
            String clinicId = tenantContext.requireClinicId();

            AppointmentResponse response = appointmentMapper.toResponse(
                    appointmentService.getById(clinicId, id)
            );

            log.info("[{}] SUCCESS GET /api/appointments/{}", traceId, id);

            return response;

        } catch (Exception ex) {
            log.error(
                    "[{}] ERROR GET /api/appointments/{} message={}",
                    traceId, id, ex.getMessage(), ex
            );
            throw ex;
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse create(@Valid @RequestBody AppointmentRequest request) {
        String traceId = UUID.randomUUID().toString();

        log.info(
                "[{}] START POST /api/appointments patientId={} therapistId={} startDateTime={} endDateTime={}",
                traceId,
                request.patientId(),
                request.therapistId(),
                request.startDateTime(),
                request.endDateTime()
        );

        try {
            String clinicId = tenantContext.requireClinicId();

            AppointmentResponse response = appointmentMapper.toResponse(
                    appointmentService.create(clinicId, request)
            );

            log.info(
                    "[{}] SUCCESS POST /api/appointments appointmentId={}",
                    traceId,
                    response.id()
            );

            return response;

        } catch (Exception ex) {
            log.error(
                    "[{}] ERROR POST /api/appointments patientId={} therapistId={} message={}",
                    traceId,
                    request.patientId(),
                    request.therapistId(),
                    ex.getMessage(),
                    ex
            );
            throw ex;
        }
    }

    @PutMapping("/{id}")
    public AppointmentResponse update(@PathVariable String id,
                                      @Valid @RequestBody AppointmentRequest request) {
        String traceId = UUID.randomUUID().toString();

        log.info(
                "[{}] START PUT /api/appointments/{} patientId={} therapistId={} startDateTime={} endDateTime={}",
                traceId,
                id,
                request.patientId(),
                request.therapistId(),
                request.startDateTime(),
                request.endDateTime()
        );

        try {
            String clinicId = tenantContext.requireClinicId();

            AppointmentResponse response = appointmentMapper.toResponse(
                    appointmentService.update(clinicId, id, request)
            );

            log.info("[{}] SUCCESS PUT /api/appointments/{}", traceId, id);

            return response;

        } catch (Exception ex) {
            log.error(
                    "[{}] ERROR PUT /api/appointments/{} message={}",
                    traceId, id, ex.getMessage(), ex
            );
            throw ex;
        }
    }

    @PatchMapping("/{id}/cancel")
    public AppointmentResponse cancel(@PathVariable String id) {
        String traceId = UUID.randomUUID().toString();

        log.info("[{}] START PATCH /api/appointments/{}/cancel", traceId, id);

        try {
            AppointmentResponse response = appointmentMapper.toResponse(
                    appointmentService.changeStatus(
                            tenantContext.requireClinicId(),
                            id,
                            AppointmentStatus.CANCELLED
                    )
            );

            log.info("[{}] SUCCESS PATCH /api/appointments/{}/cancel", traceId, id);

            return response;

        } catch (Exception ex) {
            log.error(
                    "[{}] ERROR PATCH /api/appointments/{}/cancel message={}",
                    traceId, id, ex.getMessage(), ex
            );
            throw ex;
        }
    }

    @PatchMapping("/{id}/complete")
    public AppointmentResponse complete(@PathVariable String id) {
        String traceId = UUID.randomUUID().toString();

        log.info("[{}] START PATCH /api/appointments/{}/complete", traceId, id);

        try {
            AppointmentResponse response = appointmentMapper.toResponse(
                    appointmentService.changeStatus(
                            tenantContext.requireClinicId(),
                            id,
                            AppointmentStatus.COMPLETED
                    )
            );

            log.info("[{}] SUCCESS PATCH /api/appointments/{}/complete", traceId, id);

            return response;

        } catch (Exception ex) {
            log.error(
                    "[{}] ERROR PATCH /api/appointments/{}/complete message={}",
                    traceId, id, ex.getMessage(), ex
            );
            throw ex;
        }
    }

    @PatchMapping("/{id}/no-show")
    public AppointmentResponse noShow(@PathVariable String id) {
        String traceId = UUID.randomUUID().toString();

        log.info("[{}] START PATCH /api/appointments/{}/no-show", traceId, id);

        try {
            AppointmentResponse response = appointmentMapper.toResponse(
                    appointmentService.changeStatus(
                            tenantContext.requireClinicId(),
                            id,
                            AppointmentStatus.NO_SHOW
                    )
            );

            log.info("[{}] SUCCESS PATCH /api/appointments/{}/no-show", traceId, id);

            return response;

        } catch (Exception ex) {
            log.error(
                    "[{}] ERROR PATCH /api/appointments/{}/no-show message={}",
                    traceId, id, ex.getMessage(), ex
            );
            throw ex;
        }
    }
}