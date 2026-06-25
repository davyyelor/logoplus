package com.logopeda.patient.mapper;

import com.logopeda.patient.dto.PatientResponse;
import com.logopeda.patient.model.Patient;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    public PatientResponse toResponse(Patient patient) {
        return new PatientResponse(
                patient.getId(),
                patient.getClinicId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getFullName(),
                patient.getBirthDate(),
                patient.getGender(),
                patient.getStatus(),
                patient.getMainTherapistId(),
                patient.getSchoolName(),
                patient.getReferralSource(),
                patient.getReasonForConsultation(),
                patient.getRelevantNotes(),
                patient.getCreatedAt(),
                patient.getUpdatedAt());
    }
}
