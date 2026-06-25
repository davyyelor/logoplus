// Domain types mirroring the backend DTOs. Kept in one place so pages and
// services share a single source of truth.

export type PaymentMethod =
  | "CASH"
  | "CARD"
  | "BANK_TRANSFER"
  | "BIZUM"
  | "STRIPE"
  | "OTHER";

export type PaymentStatus = "REGISTERED" | "REFUNDED" | "CANCELLED";

export type RecurrenceType = "MONTHLY" | "WEEKLY" | "CUSTOM";

export type SessionBillingStatus =
  | "PENDING"
  | "PAID"
  | "PARTIALLY_PAID"
  | "CANCELLED"
  | "NO_CHARGE";

export interface Payment {
  id: string;
  clinicId: string;
  patientId: string;
  sessionId: string | null;
  feeId: string | null;
  amount: number;
  currency: string;
  paymentDate: string; // ISO date (yyyy-MM-dd)
  method: PaymentMethod;
  status: PaymentStatus;
  notes: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface PaymentRequest {
  patientId: string;
  sessionId?: string | null;
  feeId?: string | null;
  amount: number;
  currency?: string;
  paymentDate: string;
  method: PaymentMethod;
  notes?: string | null;
}

export interface Fee {
  id: string;
  clinicId: string;
  patientId: string;
  name: string;
  amount: number;
  currency: string;
  recurrenceType: RecurrenceType;
  startDate: string;
  endDate: string | null;
  active: boolean;
  notes: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface FeeRequest {
  patientId: string;
  name: string;
  amount: number;
  currency?: string;
  recurrenceType: RecurrenceType;
  startDate: string;
  endDate?: string | null;
  notes?: string | null;
}

export interface SessionBilling {
  id: string;
  clinicId: string;
  patientId: string;
  sessionId: string;
  sessionDate: string;
  amount: number;
  currency: string;
  status: SessionBillingStatus;
  paidAmount: number;
  pendingAmount: number;
  paymentId: string | null;
  notes: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface SessionBillingRequest {
  patientId: string;
  sessionId: string;
  sessionDate: string;
  amount: number;
  currency?: string;
  paidAmount?: number;
  notes?: string | null;
}

export interface BillingSummary {
  totalPaid: number;
  totalPending: number;
  totalRefunded: number;
  numberOfPaidSessions: number;
  numberOfPendingSessions: number;
  activeFees: number;
  currency: string;
}

export const PAYMENT_METHODS: PaymentMethod[] = [
  "CASH",
  "CARD",
  "BANK_TRANSFER",
  "BIZUM",
  "STRIPE",
  "OTHER",
];

export const PAYMENT_STATUSES: PaymentStatus[] = [
  "REGISTERED",
  "REFUNDED",
  "CANCELLED",
];

export const RECURRENCE_TYPES: RecurrenceType[] = [
  "MONTHLY",
  "WEEKLY",
  "CUSTOM",
];

export const SESSION_BILLING_STATUSES: SessionBillingStatus[] = [
  "PENDING",
  "PAID",
  "PARTIALLY_PAID",
  "CANCELLED",
  "NO_CHARGE",
];
