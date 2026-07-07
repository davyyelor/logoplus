package com.logopeda.patient.dto;

import com.logopeda.patient.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/** Payload to create or update a patient. */
public record PatientRequest(
        @NotBlank(message = "firstName is required")
        String firstName,

        @NotBlank(message = "lastName is required")
        String lastName,

        LocalDate birthDate,

        Gender gender,

        @Size(max = 36)
        String mainTherapistId,

        String schoolName,

        String referralSource,

        @Size(max = 2000)
        String reasonForConsultation,

        @Size(max = 4000)
        String relevantNotes,

        @Size(max = 36)
        String centerId) {
}
