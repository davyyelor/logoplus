package com.logopeda.appointment.service;

import com.logopeda.appointment.dto.AppointmentRequest;
import com.logopeda.appointment.enums.AppointmentStatus;
import com.logopeda.appointment.enums.LocationType;
import com.logopeda.appointment.model.Appointment;
import com.logopeda.appointment.repository.AppointmentRepository;
import com.logopeda.billing.exception.BusinessValidationException;
import com.logopeda.billing.exception.ResourceNotFoundException;
import com.logopeda.patient.service.PatientAccessGuard;
import com.logopeda.shared.security.TenantContext;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Appointment scheduling and lifecycle transitions, scoped to the clinic. */
@Service
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientAccessGuard patientAccessGuard;
    private final TenantContext tenantContext;
    private final com.logopeda.center.service.CenterService centerService;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientAccessGuard patientAccessGuard, TenantContext tenantContext,
                              com.logopeda.center.service.CenterService centerService) {
        this.appointmentRepository = appointmentRepository;
        this.patientAccessGuard = patientAccessGuard;
        this.tenantContext = tenantContext;
        this.centerService = centerService;
    }

    @Transactional(readOnly = true)
    public List<Appointment> search(String clinicId, String patientId, String therapistId,
                                    Instant from, Instant to, AppointmentStatus status) {
        return appointmentRepository.search(clinicId,
                blankToNull(patientId), blankToNull(therapistId), from, to, status);
    }

    @Transactional(readOnly = true)
    public Appointment getById(String clinicId, String id) {
        return findOwned(clinicId, id);
    }

    public Appointment create(String clinicId, AppointmentRequest request) {
        validateRange(request.startDateTime(), request.endDateTime());
        patientAccessGuard.requireAccessiblePatient(request.patientId());
        Appointment appointment = new Appointment();
        appointment.setClinicId(clinicId);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        apply(appointment, request);
        return appointmentRepository.save(appointment);
    }

    public Appointment update(String clinicId, String id, AppointmentRequest request) {
        Appointment appointment = findOwned(clinicId, id);
        validateRange(request.startDateTime(), request.endDateTime());
        patientAccessGuard.requireAccessiblePatient(request.patientId());
        apply(appointment, request);
        return appointmentRepository.save(appointment);
    }

    public Appointment changeStatus(String clinicId, String id, AppointmentStatus status) {
        Appointment appointment = findOwned(clinicId, id);
        appointment.setStatus(status);
        return appointmentRepository.save(appointment);
    }

    private void apply(Appointment appointment, AppointmentRequest request) {
        appointment.setPatientId(request.patientId());
        appointment.setTherapistId(request.therapistId());
        appointment.setTitle(request.title());
        appointment.setStartDateTime(request.startDateTime());
        appointment.setEndDateTime(request.endDateTime());
        appointment.setLocationType(request.locationType() != null
                ? request.locationType() : LocationType.IN_PERSON);
        appointment.setNotes(request.notes());
        String centerId = blankToNull(request.centerId());
        centerService.requireCenterInClinic(centerId);
        appointment.setCenterId(centerId);
    }

    private void validateRange(Instant start, Instant end) {
        if (end.isBefore(start)) {
            throw new BusinessValidationException("endDateTime must be after startDateTime");
        }
    }

    private Appointment findOwned(String clinicId, String id) {
        return appointmentRepository.findByIdAndClinicId(id, clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
