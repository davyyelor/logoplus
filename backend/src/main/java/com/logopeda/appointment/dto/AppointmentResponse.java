package com.logopeda.appointment.dto;

import com.logopeda.appointment.enums.AppointmentStatus;
import com.logopeda.appointment.enums.LocationType;
import java.time.Instant;

public record AppointmentResponse(
        String id,
        String clinicId,
        String patientId,
        String therapistId,
        String title,
        Instant startDateTime,
        Instant endDateTime,
        AppointmentStatus status,
        LocationType locationType,
        String notes,
        String centerId,
        Instant createdAt,
        Instant updatedAt) {
}
