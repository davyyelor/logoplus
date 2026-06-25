import { apiGet, apiSend, buildQuery } from "./apiClient";
import type {
  SessionGoalProgress,
  SessionGoalProgressRequest,
  SessionType,
  TherapySession,
  TherapySessionRequest,
} from "../types/api";

export interface SessionFilters {
  patientId?: string;
  therapistId?: string;
  fromDate?: string;
  toDate?: string;
  sessionType?: SessionType | "";
}

export const sessionService = {
  list(filters: SessionFilters = {}): Promise<TherapySession[]> {
    return apiGet<TherapySession[]>(`/sessions${buildQuery(filters)}`);
  },

  get(id: string): Promise<TherapySession> {
    return apiGet<TherapySession>(`/sessions/${id}`);
  },

  create(request: TherapySessionRequest): Promise<TherapySession> {
    return apiSend<TherapySession>("/sessions", "POST", request);
  },

  update(id: string, request: TherapySessionRequest): Promise<TherapySession> {
    return apiSend<TherapySession>(`/sessions/${id}`, "PUT", request);
  },

  remove(id: string): Promise<void> {
    return apiSend<void>(`/sessions/${id}`, "DELETE");
  },

  listProgress(sessionId: string): Promise<SessionGoalProgress[]> {
    return apiGet<SessionGoalProgress[]>(`/sessions/${sessionId}/goal-progress`);
  },

  addProgress(sessionId: string, request: SessionGoalProgressRequest): Promise<SessionGoalProgress> {
    return apiSend<SessionGoalProgress>(`/sessions/${sessionId}/goal-progress`, "POST", request);
  },

  updateProgress(id: string, request: SessionGoalProgressRequest): Promise<SessionGoalProgress> {
    return apiSend<SessionGoalProgress>(`/session-goal-progress/${id}`, "PUT", request);
  },

  removeProgress(id: string): Promise<void> {
    return apiSend<void>(`/session-goal-progress/${id}`, "DELETE");
  },
};
