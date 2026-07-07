package com.logopeda.evolution.service;

import com.logopeda.billing.exception.BusinessValidationException;
import com.logopeda.billing.exception.ResourceNotFoundException;
import com.logopeda.evolution.dto.MetricEvolutionResponse;
import com.logopeda.evolution.dto.PatientMetricEntryRequest;
import com.logopeda.evolution.dto.PatientMetricRequest;
import com.logopeda.evolution.mapper.EvolutionMapper;
import com.logopeda.evolution.model.PatientMetric;
import com.logopeda.evolution.model.PatientMetricEntry;
import com.logopeda.evolution.repository.PatientMetricEntryRepository;
import com.logopeda.evolution.repository.PatientMetricRepository;
import com.logopeda.patient.service.PatientAccessGuard;
import com.logopeda.shared.enums.Role;
import com.logopeda.shared.security.TenantContext;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Visual evolution: named metrics and their historical entries. This module only
 * stores and returns data for charting — it performs no clinical inference,
 * diagnosis or recommendation. Access is scoped by clinic and
 * {@link PatientAccessGuard}; FAMILY users only see metrics marked visible.
 */
@Service
@Transactional
public class EvolutionService {

    private final PatientMetricRepository metricRepository;
    private final PatientMetricEntryRepository entryRepository;
    private final PatientAccessGuard patientAccessGuard;
    private final TenantContext tenantContext;
    private final EvolutionMapper evolutionMapper;

    public EvolutionService(PatientMetricRepository metricRepository,
                            PatientMetricEntryRepository entryRepository,
                            PatientAccessGuard patientAccessGuard, TenantContext tenantContext,
                            EvolutionMapper evolutionMapper) {
        this.metricRepository = metricRepository;
        this.entryRepository = entryRepository;
        this.patientAccessGuard = patientAccessGuard;
        this.tenantContext = tenantContext;
        this.evolutionMapper = evolutionMapper;
    }

    @Transactional(readOnly = true)
    public List<PatientMetric> listMetrics(String patientId) {
        patientAccessGuard.requireAccessiblePatient(patientId);
        String clinicId = tenantContext.requireClinicId();
        if (tenantContext.currentRole() == Role.FAMILY) {
            return metricRepository.findByPatientIdAndClinicIdAndVisibleToFamilyTrueOrderByCreatedAtDesc(
                    patientId, clinicId);
        }
        return metricRepository.findByPatientIdAndClinicIdOrderByCreatedAtDesc(patientId, clinicId);
    }

    public PatientMetric createMetric(String patientId, PatientMetricRequest request) {
        patientAccessGuard.requireAccessiblePatient(patientId);
        PatientMetric metric = new PatientMetric();
        metric.setClinicId(tenantContext.requireClinicId());
        metric.setPatientId(patientId);
        applyMetric(metric, request);
        return metricRepository.save(metric);
    }

    public PatientMetric updateMetric(String metricId, PatientMetricRequest request) {
        PatientMetric metric = findOwnedMetric(metricId);
        applyMetric(metric, request);
        return metricRepository.save(metric);
    }

    public PatientMetricEntry addEntry(String metricId, PatientMetricEntryRequest request) {
        PatientMetric metric = findOwnedMetric(metricId);
        PatientMetricEntry entry = new PatientMetricEntry();
        entry.setClinicId(metric.getClinicId());
        entry.setPatientId(metric.getPatientId());
        entry.setMetricId(metric.getId());
        entry.setSessionId(request.sessionId());
        entry.setValue(request.value());
        entry.setEntryDate(request.entryDate());
        entry.setNotes(request.notes());
        return entryRepository.save(entry);
    }

    /** Returns, per metric, its ordered historical entries (respecting visibility). */
    @Transactional(readOnly = true)
    public List<MetricEvolutionResponse> evolution(String patientId) {
        List<PatientMetric> metrics = listMetrics(patientId);
        String clinicId = tenantContext.requireClinicId();
        return metrics.stream()
                .map(metric -> new MetricEvolutionResponse(
                        evolutionMapper.toResponse(metric),
                        entryRepository.findByMetricIdAndClinicIdOrderByEntryDateAsc(metric.getId(), clinicId)
                                .stream().map(evolutionMapper::toResponse).toList()))
                .toList();
    }

    private void applyMetric(PatientMetric metric, PatientMetricRequest request) {
        if (request.name() == null || request.name().isBlank()) {
            throw new BusinessValidationException("Metric name is required");
        }
        metric.setName(request.name().trim());
        metric.setDescription(request.description());
        metric.setUnit(request.unit());
        if (request.active() != null) {
            metric.setActive(request.active());
        }
        if (request.visibleToFamily() != null) {
            metric.setVisibleToFamily(request.visibleToFamily());
        }
    }

    private PatientMetric findOwnedMetric(String metricId) {
        PatientMetric metric = metricRepository.findByIdAndClinicId(metricId, tenantContext.requireClinicId())
                .orElseThrow(() -> new ResourceNotFoundException("PatientMetric", metricId));
        patientAccessGuard.requireAccessiblePatient(metric.getPatientId());
        return metric;
    }
}
