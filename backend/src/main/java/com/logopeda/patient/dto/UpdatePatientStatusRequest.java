package com.logopeda.patient.dto;

import com.logopeda.patient.enums.PatientStatus;
import jakarta.validation.constraints.NotNull;

public record UpdatePatientStatusRequest(
        @NotNull(message = "status is required")
        PatientStatus status) {
}
