import { apiGet, apiSend, buildQuery } from "./apiClient";
import type { Appointment, AppointmentRequest, AppointmentStatus } from "../types/api";

export interface AppointmentFilters {
  patientId?: string;
  therapistId?: string;
  from?: string;
  to?: string;
  status?: AppointmentStatus | "";
}

export const appointmentService = {
  list(filters: AppointmentFilters = {}): Promise<Appointment[]> {
    return apiGet<Appointment[]>(`/appointments${buildQuery(filters)}`);
  },

  get(id: string): Promise<Appointment> {
    return apiGet<Appointment>(`/appointments/${id}`);
  },

  create(request: AppointmentRequest): Promise<Appointment> {
    return apiSend<Appointment>("/appointments", "POST", request);
  },

  update(id: string, request: AppointmentRequest): Promise<Appointment> {
    return apiSend<Appointment>(`/appointments/${id}`, "PUT", request);
  },

  cancel(id: string): Promise<Appointment> {
    return apiSend<Appointment>(`/appointments/${id}/cancel`, "PATCH");
  },

  complete(id: string): Promise<Appointment> {
    return apiSend<Appointment>(`/appointments/${id}/complete`, "PATCH");
  },

  noShow(id: string): Promise<Appointment> {
    return apiSend<Appointment>(`/appointments/${id}/no-show`, "PATCH");
  },
};
