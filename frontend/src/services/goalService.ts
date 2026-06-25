import { apiGet, apiSend } from "./apiClient";
import type { GoalStatus, TherapyGoal, TherapyGoalRequest } from "../types/api";

export const goalService = {
  listForPatient(patientId: string): Promise<TherapyGoal[]> {
    return apiGet<TherapyGoal[]>(`/patients/${patientId}/goals`);
  },

  get(id: string): Promise<TherapyGoal> {
    return apiGet<TherapyGoal>(`/goals/${id}`);
  },

  create(patientId: string, request: TherapyGoalRequest): Promise<TherapyGoal> {
    return apiSend<TherapyGoal>(`/patients/${patientId}/goals`, "POST", request);
  },

  update(id: string, request: TherapyGoalRequest): Promise<TherapyGoal> {
    return apiSend<TherapyGoal>(`/goals/${id}`, "PUT", request);
  },

  setStatus(id: string, status: GoalStatus): Promise<TherapyGoal> {
    return apiSend<TherapyGoal>(`/goals/${id}/status`, "PATCH", { status });
  },

  remove(id: string): Promise<void> {
    return apiSend<void>(`/goals/${id}`, "DELETE");
  },
};
