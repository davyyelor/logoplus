package com.logopeda.patient.dto;

import com.logopeda.patient.enums.Gender;
import com.logopeda.patient.enums.PatientStatus;
import java.time.Instant;
import java.time.LocalDate;

public record PatientResponse(
        String id,
        String clinicId,
        String firstName,
        String lastName,
        String fullName,
        LocalDate birthDate,
        Gender gender,
        PatientStatus status,
        String mainTherapistId,
        String schoolName,
        String referralSource,
        String reasonForConsultation,
        String relevantNotes,
        String centerId,
        Instant createdAt,
        Instant updatedAt) {
}
