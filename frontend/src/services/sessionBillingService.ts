import { apiGet, apiSend, buildQuery } from "./apiClient";
import type {
  SessionBilling,
  SessionBillingRequest,
  SessionBillingStatus,
} from "../types/billing";

export interface SessionBillingFilters {
  patientId?: string;
  fromDate?: string;
  toDate?: string;
  status?: SessionBillingStatus | "";
}

export const sessionBillingService = {
  list(filters: SessionBillingFilters = {}): Promise<SessionBilling[]> {
    return apiGet<SessionBilling[]>(`/session-billing${buildQuery(filters)}`);
  },

  get(id: string): Promise<SessionBilling> {
    return apiGet<SessionBilling>(`/session-billing/${id}`);
  },

  create(request: SessionBillingRequest): Promise<SessionBilling> {
    return apiSend<SessionBilling>("/session-billing", "POST", request);
  },

  update(id: string, request: SessionBillingRequest): Promise<SessionBilling> {
    return apiSend<SessionBilling>(`/session-billing/${id}`, "PUT", request);
  },

  markPaid(id: string): Promise<SessionBilling> {
    return apiSend<SessionBilling>(`/session-billing/${id}/mark-paid`, "PATCH");
  },

  markPending(id: string): Promise<SessionBilling> {
    return apiSend<SessionBilling>(`/session-billing/${id}/mark-pending`, "PATCH");
  },

  markNoCharge(id: string): Promise<SessionBilling> {
    return apiSend<SessionBilling>(`/session-billing/${id}/mark-no-charge`, "PATCH");
  },
};
