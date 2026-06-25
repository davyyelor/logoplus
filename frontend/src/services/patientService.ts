import { apiGet, apiSend, buildQuery } from "./apiClient";
import type { Patient, PatientRequest, PatientStatus } from "../types/api";

export interface PatientFilters {
  status?: PatientStatus | "";
  therapistId?: string;
  search?: string;
}

export const patientService = {
  list(filters: PatientFilters = {}): Promise<Patient[]> {
    return apiGet<Patient[]>(`/patients${buildQuery(filters)}`);
  },

  get(id: string): Promise<Patient> {
    return apiGet<Patient>(`/patients/${id}`);
  },

  create(request: PatientRequest): Promise<Patient> {
    return apiSend<Patient>("/patients", "POST", request);
  },

  update(id: string, request: PatientRequest): Promise<Patient> {
    return apiSend<Patient>(`/patients/${id}`, "PUT", request);
  },

  setStatus(id: string, status: PatientStatus): Promise<Patient> {
    return apiSend<Patient>(`/patients/${id}/status`, "PATCH", { status });
  },

  remove(id: string): Promise<Patient> {
    return apiSend<Patient>(`/patients/${id}`, "DELETE");
  },
};
