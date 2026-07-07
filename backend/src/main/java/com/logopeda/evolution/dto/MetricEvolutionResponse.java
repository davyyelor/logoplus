package com.logopeda.evolution.dto;

import java.util.List;

/** A metric together with its ordered historical entries, for charting. */
public record MetricEvolutionResponse(
        PatientMetricResponse metric,
        List<PatientMetricEntryResponse> entries) {
}
