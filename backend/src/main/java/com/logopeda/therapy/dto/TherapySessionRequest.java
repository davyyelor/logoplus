package com.logopeda.therapy.dto;

import com.logopeda.therapy.enums.SessionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/** Payload to create or update a therapy session. */
public record TherapySessionRequest(
        @NotBlank(message = "patientId is required")
        String patientId,

        @NotBlank(message = "therapistId is required")
        String therapistId,

        @Size(max = 36)
        String appointmentId,

        @NotNull(message = "sessionDate is required")
        LocalDate sessionDate,

        @Positive(message = "durationMinutes must be positive")
        Integer durationMinutes,

        @NotNull(message = "sessionType is required")
        SessionType sessionType,

        @Size(max = 4000)
        String summary,

        @Size(max = 4000)
        String activitiesPerformed,

        @Size(max = 4000)
        String patientResponse,

        @Size(max = 4000)
        String observations,

        @Size(max = 4000)
        String homework,

        @Size(max = 4000)
        String nextSteps,

        @Size(max = 36)
        String centerId) {
}
