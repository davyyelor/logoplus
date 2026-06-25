import { apiGet, buildQuery, downloadCsv } from "./apiClient";
import type { BillingSummary } from "../types/billing";

export interface SummaryFilters {
  fromDate?: string;
  toDate?: string;
}

export const billingService = {
  summary(filters: SummaryFilters = {}): Promise<BillingSummary> {
    return apiGet<BillingSummary>(`/billing/summary${buildQuery(filters)}`);
  },

  exportPayments(filters: { patientId?: string; fromDate?: string; toDate?: string } = {}) {
    return downloadCsv(`/exports/payments.csv${buildQuery(filters)}`, "payments.csv");
  },

  exportPendingSessions(filters: { patientId?: string; fromDate?: string; toDate?: string } = {}) {
    return downloadCsv(
      `/exports/pending-sessions.csv${buildQuery(filters)}`,
      "pending-sessions.csv",
    );
  },

  exportFees(filters: { patientId?: string; active?: boolean | "" } = {}) {
    return downloadCsv(`/exports/fees.csv${buildQuery(filters)}`, "fees.csv");
  },

  exportSummary(filters: SummaryFilters = {}) {
    return downloadCsv(`/exports/billing-summary.csv${buildQuery(filters)}`, "billing-summary.csv");
  },
};
