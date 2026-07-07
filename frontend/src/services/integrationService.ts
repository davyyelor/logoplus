import { apiGet, apiSend, downloadFile } from "./apiClient";
import type {
  CalendarStatus,
  CalendarSyncResult,
  CheckoutRequest,
  CheckoutSessionResult,
  ConnectAccountResult,
  StripeStatus,
} from "../types/api";

export const stripeService = {
  status(): Promise<StripeStatus> {
    return apiGet<StripeStatus>("/billing/stripe/status");
  },

  checkout(request: CheckoutRequest): Promise<CheckoutSessionResult> {
    return apiSend<CheckoutSessionResult>("/billing/stripe/checkout", "POST", request);
  },

  onboard(returnUrl?: string): Promise<ConnectAccountResult> {
    const path = returnUrl
      ? `/billing/stripe/connect/onboard?returnUrl=${encodeURIComponent(returnUrl)}`
      : "/billing/stripe/connect/onboard";
    return apiSend<ConnectAccountResult>(path, "POST");
  },
};

export const calendarService = {
  status(): Promise<CalendarStatus> {
    return apiGet<CalendarStatus>("/calendar/status");
  },

  exportIcs(appointmentId: string): Promise<void> {
    return downloadFile(
      `/appointments/${appointmentId}/calendar.ics`,
      `cita-${appointmentId}.ics`,
    );
  },

  sync(appointmentId: string): Promise<CalendarSyncResult> {
    return apiSend<CalendarSyncResult>(`/appointments/${appointmentId}/calendar/sync`, "POST");
  },
};
