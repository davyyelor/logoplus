package com.logopeda.appointment.mapper;

import com.logopeda.appointment.dto.AppointmentResponse;
import com.logopeda.appointment.model.Appointment;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public AppointmentResponse toResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getClinicId(),
                appointment.getPatientId(),
                appointment.getTherapistId(),
                appointment.getTitle(),
                appointment.getStartDateTime(),
                appointment.getEndDateTime(),
                appointment.getStatus(),
                appointment.getLocationType(),
                appointment.getNotes(),
                appointment.getCenterId(),
                appointment.getCreatedAt(),
                appointment.getUpdatedAt());
    }
}
