import { apiGet, apiSend } from "./apiClient";
import type {
  MetricEvolution,
  PatientMetric,
  PatientMetricEntry,
  PatientMetricEntryRequest,
  PatientMetricRequest,
} from "../types/api";

export const evolutionService = {
  listMetrics(patientId: string): Promise<PatientMetric[]> {
    return apiGet<PatientMetric[]>(`/patients/${patientId}/metrics`);
  },

  createMetric(patientId: string, request: PatientMetricRequest): Promise<PatientMetric> {
    return apiSend<PatientMetric>(`/patients/${patientId}/metrics`, "POST", request);
  },

  updateMetric(metricId: string, request: PatientMetricRequest): Promise<PatientMetric> {
    return apiSend<PatientMetric>(`/patient-metrics/${metricId}`, "PUT", request);
  },

  addEntry(metricId: string, request: PatientMetricEntryRequest): Promise<PatientMetricEntry> {
    return apiSend<PatientMetricEntry>(`/patient-metrics/${metricId}/entries`, "POST", request);
  },

  evolution(patientId: string): Promise<MetricEvolution[]> {
    return apiGet<MetricEvolution[]>(`/patients/${patientId}/evolution`);
  },

  // Family portal
  familyEvolution(patientId: string): Promise<MetricEvolution[]> {
    return apiGet<MetricEvolution[]>(`/family/patients/${patientId}/evolution`);
  },
};
