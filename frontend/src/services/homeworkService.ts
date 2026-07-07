import { apiGet, apiSend } from "./apiClient";
import type { Homework, HomeworkRequest, HomeworkStatus } from "../types/api";

export const homeworkService = {
  listForPatient(patientId: string): Promise<Homework[]> {
    return apiGet<Homework[]>(`/patients/${patientId}/homework`);
  },

  get(id: string): Promise<Homework> {
    return apiGet<Homework>(`/homework/${id}`);
  },

  create(patientId: string, request: HomeworkRequest): Promise<Homework> {
    return apiSend<Homework>(`/patients/${patientId}/homework`, "POST", request);
  },

  update(id: string, request: HomeworkRequest): Promise<Homework> {
    return apiSend<Homework>(`/homework/${id}`, "PUT", request);
  },

  setStatus(id: string, status: HomeworkStatus): Promise<Homework> {
    return apiSend<Homework>(`/homework/${id}/status`, "PATCH", { status });
  },

  remove(id: string): Promise<void> {
    return apiSend<void>(`/homework/${id}`, "DELETE");
  },

  // Family portal
  listForFamily(patientId: string): Promise<Homework[]> {
    return apiGet<Homework[]>(`/family/patients/${patientId}/homework`);
  },

  setStatusAsFamily(id: string, status: HomeworkStatus): Promise<Homework> {
    return apiSend<Homework>(`/family/homework/${id}/status`, "PATCH", { status });
  },
};
