package com.logopeda.evolution.mapper;

import com.logopeda.evolution.dto.PatientMetricEntryResponse;
import com.logopeda.evolution.dto.PatientMetricResponse;
import com.logopeda.evolution.model.PatientMetric;
import com.logopeda.evolution.model.PatientMetricEntry;
import org.springframework.stereotype.Component;

@Component
public class EvolutionMapper {

    public PatientMetricResponse toResponse(PatientMetric metric) {
        return new PatientMetricResponse(
                metric.getId(),
                metric.getClinicId(),
                metric.getPatientId(),
                metric.getName(),
                metric.getDescription(),
                metric.getUnit(),
                metric.isActive(),
                metric.isVisibleToFamily(),
                metric.getCreatedAt(),
                metric.getUpdatedAt());
    }

    public PatientMetricEntryResponse toResponse(PatientMetricEntry entry) {
        return new PatientMetricEntryResponse(
                entry.getId(),
                entry.getClinicId(),
                entry.getPatientId(),
                entry.getMetricId(),
                entry.getSessionId(),
                entry.getValue(),
                entry.getEntryDate(),
                entry.getNotes(),
                entry.getCreatedAt());
    }
}
