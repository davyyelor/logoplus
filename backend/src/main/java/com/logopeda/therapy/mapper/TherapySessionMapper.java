package com.logopeda.therapy.mapper;

import com.logopeda.therapy.dto.TherapySessionResponse;
import com.logopeda.therapy.model.TherapySession;
import org.springframework.stereotype.Component;

@Component
public class TherapySessionMapper {

    public TherapySessionResponse toResponse(TherapySession session) {
        return new TherapySessionResponse(
                session.getId(),
                session.getClinicId(),
                session.getPatientId(),
                session.getTherapistId(),
                session.getAppointmentId(),
                session.getSessionDate(),
                session.getDurationMinutes(),
                session.getSessionType(),
                session.getSummary(),
                session.getActivitiesPerformed(),
                session.getPatientResponse(),
                session.getObservations(),
                session.getHomework(),
                session.getNextSteps(),
                session.getCreatedAt(),
                session.getUpdatedAt());
    }
}
