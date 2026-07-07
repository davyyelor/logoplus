package com.logopeda.appointment.dto;

import com.logopeda.appointment.enums.LocationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

/** Payload to create or update an appointment. */
public record AppointmentRequest(
        @NotBlank(message = "patientId is required")
        String patientId,

        @NotBlank(message = "therapistId is required")
        String therapistId,

        String title,

        @NotNull(message = "startDateTime is required")
        Instant startDateTime,

        @NotNull(message = "endDateTime is required")
        Instant endDateTime,

        LocationType locationType,

        String notes,

        String centerId) {
}
