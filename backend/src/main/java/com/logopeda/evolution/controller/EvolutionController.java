package com.logopeda.evolution.controller;

import com.logopeda.evolution.dto.MetricEvolutionResponse;
import com.logopeda.evolution.dto.PatientMetricEntryRequest;
import com.logopeda.evolution.dto.PatientMetricEntryResponse;
import com.logopeda.evolution.dto.PatientMetricRequest;
import com.logopeda.evolution.dto.PatientMetricResponse;
import com.logopeda.evolution.mapper.EvolutionMapper;
import com.logopeda.evolution.service.EvolutionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Visual evolution endpoints. Metric definitions and entries are managed by
 * clinical staff; reads of the evolution view are also available to RECEPTION.
 * No clinical inference is performed — data is returned as-is for charting.
 */
@RestController
public class EvolutionController {

    private final EvolutionService evolutionService;
    private final EvolutionMapper evolutionMapper;

    public EvolutionController(EvolutionService evolutionService, EvolutionMapper evolutionMapper) {
        this.evolutionService = evolutionService;
        this.evolutionMapper = evolutionMapper;
    }

    @GetMapping("/api/patients/{patientId}/metrics")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST','RECEPTION')")
    public List<PatientMetricResponse> listMetrics(@PathVariable String patientId) {
        return evolutionService.listMetrics(patientId).stream().map(evolutionMapper::toResponse).toList();
    }

    @PostMapping("/api/patients/{patientId}/metrics")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST')")
    @ResponseStatus(HttpStatus.CREATED)
    public PatientMetricResponse createMetric(@PathVariable String patientId,
                                              @Valid @RequestBody PatientMetricRequest request) {
        return evolutionMapper.toResponse(evolutionService.createMetric(patientId, request));
    }

    @PutMapping("/api/patient-metrics/{metricId}")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST')")
    public PatientMetricResponse updateMetric(@PathVariable String metricId,
                                              @Valid @RequestBody PatientMetricRequest request) {
        return evolutionMapper.toResponse(evolutionService.updateMetric(metricId, request));
    }

    @PostMapping("/api/patient-metrics/{metricId}/entries")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST')")
    @ResponseStatus(HttpStatus.CREATED)
    public PatientMetricEntryResponse addEntry(@PathVariable String metricId,
                                               @Valid @RequestBody PatientMetricEntryRequest request) {
        return evolutionMapper.toResponse(evolutionService.addEntry(metricId, request));
    }

    @GetMapping("/api/patients/{patientId}/evolution")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST','RECEPTION')")
    public List<MetricEvolutionResponse> evolution(@PathVariable String patientId) {
        return evolutionService.evolution(patientId);
    }
}
