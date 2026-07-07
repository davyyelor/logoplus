import { apiGet, apiSend, downloadFile } from "./apiClient";
import type {
  ClinicalHistoryExportFormat,
  Reminder,
  ReminderRequest,
} from "../types/api";

export const reminderService = {
  list(): Promise<Reminder[]> {
    return apiGet<Reminder[]>("/reminders");
  },

  get(id: string): Promise<Reminder> {
    return apiGet<Reminder>(`/reminders/${id}`);
  },

  create(request: ReminderRequest): Promise<Reminder> {
    return apiSend<Reminder>("/reminders", "POST", request);
  },

  update(id: string, request: ReminderRequest): Promise<Reminder> {
    return apiSend<Reminder>(`/reminders/${id}`, "PUT", request);
  },

  cancel(id: string): Promise<Reminder> {
    return apiSend<Reminder>(`/reminders/${id}/cancel`, "PATCH");
  },

  remove(id: string): Promise<void> {
    return apiSend<void>(`/reminders/${id}`, "DELETE");
  },

  // Family portal
  listForFamily(): Promise<Reminder[]> {
    return apiGet<Reminder[]>("/family/reminders");
  },
};

const EXTENSION: Record<ClinicalHistoryExportFormat, string> = {
  ZIP: "zip",
  PDF: "pdf",
  CSV: "csv",
};

export const clinicalHistoryService = {
  export(patientId: string, format: ClinicalHistoryExportFormat): Promise<void> {
    return downloadFile(
      `/patients/${patientId}/clinical-history/export?format=${format}`,
      `historia-clinica-${patientId}.${EXTENSION[format]}`,
    );
  },
};
