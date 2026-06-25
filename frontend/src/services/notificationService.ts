import { apiGet, apiSend } from "./apiClient";
import type { AppNotification } from "../types/api";

export const notificationService = {
  list(): Promise<AppNotification[]> {
    return apiGet<AppNotification[]>("/notifications");
  },

  unreadCount(): Promise<number> {
    return apiGet<{ count: number }>("/notifications/unread-count").then((r) => r.count);
  },

  markRead(id: string): Promise<AppNotification> {
    return apiSend<AppNotification>(`/notifications/${id}/read`, "PATCH");
  },

  markAllRead(): Promise<void> {
    return apiSend<void>("/notifications/read-all", "PATCH");
  },
};
