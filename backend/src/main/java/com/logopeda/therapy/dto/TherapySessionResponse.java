package com.logopeda.therapy.dto;

import com.logopeda.therapy.enums.SessionType;
import java.time.Instant;
import java.time.LocalDate;

public record TherapySessionResponse(
        String id,
        String clinicId,
        String patientId,
        String therapistId,
        String appointmentId,
        LocalDate sessionDate,
        Integer durationMinutes,
        SessionType sessionType,
        String summary,
        String activitiesPerformed,
        String patientResponse,
        String observations,
        String homework,
        String nextSteps,
        Instant createdAt,
        Instant updatedAt) {
}
