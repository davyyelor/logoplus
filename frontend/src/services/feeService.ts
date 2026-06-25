import { apiGet, apiSend, buildQuery } from "./apiClient";
import type { Fee, FeeRequest } from "../types/billing";

export interface FeeFilters {
  patientId?: string;
  active?: boolean | "";
}

export const feeService = {
  list(filters: FeeFilters = {}): Promise<Fee[]> {
    return apiGet<Fee[]>(`/fees${buildQuery(filters)}`);
  },

  get(id: string): Promise<Fee> {
    return apiGet<Fee>(`/fees/${id}`);
  },

  create(request: FeeRequest): Promise<Fee> {
    return apiSend<Fee>("/fees", "POST", request);
  },

  update(id: string, request: FeeRequest): Promise<Fee> {
    return apiSend<Fee>(`/fees/${id}`, "PUT", request);
  },

  activate(id: string): Promise<Fee> {
    return apiSend<Fee>(`/fees/${id}/activate`, "PATCH");
  },

  deactivate(id: string): Promise<Fee> {
    return apiSend<Fee>(`/fees/${id}/deactivate`, "PATCH");
  },
};
