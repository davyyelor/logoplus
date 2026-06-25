import { apiGet, apiSend, buildQuery } from "./apiClient";
import type { Payment, PaymentMethod, PaymentRequest, PaymentStatus } from "../types/billing";

export interface PaymentFilters {
  patientId?: string;
  fromDate?: string;
  toDate?: string;
  method?: PaymentMethod | "";
  status?: PaymentStatus | "";
}

export const paymentService = {
  list(filters: PaymentFilters = {}): Promise<Payment[]> {
    return apiGet<Payment[]>(`/payments${buildQuery(filters)}`);
  },

  get(id: string): Promise<Payment> {
    return apiGet<Payment>(`/payments/${id}`);
  },

  create(request: PaymentRequest): Promise<Payment> {
    return apiSend<Payment>("/payments", "POST", request);
  },

  update(id: string, request: PaymentRequest): Promise<Payment> {
    return apiSend<Payment>(`/payments/${id}`, "PUT", request);
  },

  cancel(id: string): Promise<Payment> {
    return apiSend<Payment>(`/payments/${id}`, "DELETE");
  },

  refund(id: string): Promise<Payment> {
    return apiSend<Payment>(`/payments/${id}/refund`, "POST");
  },
};
