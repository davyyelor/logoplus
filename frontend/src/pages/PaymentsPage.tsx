import { useState } from "react";
import { Modal } from "../components/Modal";
import { StateView } from "../components/StateView";
import { StatusBadge } from "../components/StatusBadge";
import { useAsync } from "../hooks/useAsync";
import {
  paymentService,
  type PaymentFilters,
} from "../services/paymentService";
import {
  PAYMENT_METHODS,
  PAYMENT_STATUSES,
  type Payment,
  type PaymentMethod,
  type PaymentRequest,
} from "../types/billing";
import { formatCurrency, formatDate, humanizeEnum, todayIso } from "../utils/format";

interface FormState {
  patientId: string;
  sessionId: string;
  feeId: string;
  amount: string;
  currency: string;
  paymentDate: string;
  method: PaymentMethod;
  notes: string;
}

const EMPTY_FORM: FormState = {
  patientId: "",
  sessionId: "",
  feeId: "",
  amount: "",
  currency: "EUR",
  paymentDate: todayIso(),
  method: "CASH",
  notes: "",
};

export function PaymentsPage() {
  const [filters, setFilters] = useState<PaymentFilters>({});
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<Payment | null>(null);
  const [form, setForm] = useState<FormState>(EMPTY_FORM);
  const [formError, setFormError] = useState<string | null>(null);
  const [toast, setToast] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const { data, loading, error, reload } = useAsync(
    () => paymentService.list(filters),
    [filters],
  );

  function openCreate() {
    setEditing(null);
    setForm(EMPTY_FORM);
    setFormError(null);
    setModalOpen(true);
  }

  function openEdit(payment: Payment) {
    setEditing(payment);
    setForm({
      patientId: payment.patientId,
      sessionId: payment.sessionId ?? "",
      feeId: payment.feeId ?? "",
      amount: String(payment.amount),
      currency: payment.currency,
      paymentDate: payment.paymentDate,
      method: payment.method,
      notes: payment.notes ?? "",
    });
    setFormError(null);
    setModalOpen(true);
  }

  async function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    setFormError(null);
    const amount = Number(form.amount);
    if (!form.patientId.trim()) {
      setFormError("Patient is required");
      return;
    }
    if (!Number.isFinite(amount) || amount <= 0) {
      setFormError("Amount must be positive");
      return;
    }
    const request: PaymentRequest = {
      patientId: form.patientId.trim(),
      sessionId: form.sessionId.trim() || null,
      feeId: form.feeId.trim() || null,
      amount,
      currency: form.currency || "EUR",
      paymentDate: form.paymentDate,
      method: form.method,
      notes: form.notes.trim() || null,
    };
    setSaving(true);
    try {
      if (editing) {
        await paymentService.update(editing.id, request);
        setToast("Payment updated");
      } else {
        await paymentService.create(request);
        setToast("Payment created");
      }
      setModalOpen(false);
      reload();
    } catch (err) {
      setFormError(err instanceof Error ? err.message : "Save failed");
    } finally {
      setSaving(false);
    }
  }

  async function runAction(action: () => Promise<unknown>, message: string) {
    try {
      await action();
      setToast(message);
      reload();
    } catch (err) {
      setToast(null);
      window.alert(err instanceof Error ? err.message : "Action failed");
    }
  }

  return (
    <div>
      <div className="page-header">
        <h1>Payments</h1>
        <button className="btn btn--primary" onClick={openCreate}>
          New payment
        </button>
      </div>

      {toast && <div className="toast">{toast}</div>}

      <div className="card">
        <div className="filters">
          <label className="field">
            Patient
            <input
              value={filters.patientId ?? ""}
              onChange={(e) => setFilters((f) => ({ ...f, patientId: e.target.value || undefined }))}
              placeholder="patientId"
            />
          </label>
          <label className="field">
            From
            <input
              type="date"
              value={filters.fromDate ?? ""}
              onChange={(e) => setFilters((f) => ({ ...f, fromDate: e.target.value || undefined }))}
            />
          </label>
          <label className="field">
            To
            <input
              type="date"
              value={filters.toDate ?? ""}
              onChange={(e) => setFilters((f) => ({ ...f, toDate: e.target.value || undefined }))}
            />
          </label>
          <label className="field">
            Method
            <select
              value={filters.method ?? ""}
              onChange={(e) =>
                setFilters((f) => ({ ...f, method: (e.target.value as PaymentMethod) || undefined }))
              }
            >
              <option value="">All</option>
              {PAYMENT_METHODS.map((m) => (
                <option key={m} value={m}>
                  {humanizeEnum(m)}
                </option>
              ))}
            </select>
          </label>
          <label className="field">
            Status
            <select
              value={filters.status ?? ""}
              onChange={(e) =>
                setFilters((f) => ({ ...f, status: (e.target.value as Payment["status"]) || undefined }))
              }
            >
              <option value="">All</option>
              {PAYMENT_STATUSES.map((s) => (
                <option key={s} value={s}>
                  {humanizeEnum(s)}
                </option>
              ))}
            </select>
          </label>
          <button className="btn btn--ghost" onClick={() => setFilters({})}>
            Clear
          </button>
        </div>
      </div>

      <StateView
        loading={loading}
        error={error}
        isEmpty={!data || data.length === 0}
        emptyMessage="No payments found."
      >
        <table>
          <thead>
            <tr>
              <th>Date</th>
              <th>Patient</th>
              <th>Amount</th>
              <th>Method</th>
              <th>Status</th>
              <th>Notes</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {data?.map((payment) => (
              <tr key={payment.id}>
                <td>{formatDate(payment.paymentDate)}</td>
                <td>{payment.patientId}</td>
                <td className="amount">{formatCurrency(payment.amount, payment.currency)}</td>
                <td>{humanizeEnum(payment.method)}</td>
                <td>
                  <StatusBadge status={payment.status} />
                </td>
                <td className="text-muted">{payment.notes ?? "—"}</td>
                <td>
                  <div className="btn-row">
                    {payment.status === "REGISTERED" && (
                      <>
                        <button className="btn btn--sm" onClick={() => openEdit(payment)}>
                          Edit
                        </button>
                        <button
                          className="btn btn--sm"
                          onClick={() =>
                            runAction(() => paymentService.refund(payment.id), "Payment refunded")
                          }
                        >
                          Refund
                        </button>
                        <button
                          className="btn btn--sm"
                          onClick={() =>
                            runAction(() => paymentService.cancel(payment.id), "Payment cancelled")
                          }
                        >
                          Cancel
                        </button>
                      </>
                    )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </StateView>

      <Modal
        open={modalOpen}
        title={editing ? "Edit payment" : "New payment"}
        onClose={() => setModalOpen(false)}
      >
        <form onSubmit={handleSubmit}>
          <div className="form-grid">
            <label className="field">
              Patient *
              <input
                value={form.patientId}
                onChange={(e) => setForm((f) => ({ ...f, patientId: e.target.value }))}
                required
              />
            </label>
            <label className="field">
              Amount *
              <input
                type="number"
                step="0.01"
                min="0"
                value={form.amount}
                onChange={(e) => setForm((f) => ({ ...f, amount: e.target.value }))}
                required
              />
            </label>
            <label className="field">
              Currency
              <input
                value={form.currency}
                maxLength={3}
                onChange={(e) => setForm((f) => ({ ...f, currency: e.target.value.toUpperCase() }))}
              />
            </label>
            <label className="field">
              Payment date *
              <input
                type="date"
                value={form.paymentDate}
                onChange={(e) => setForm((f) => ({ ...f, paymentDate: e.target.value }))}
                required
              />
            </label>
            <label className="field">
              Method *
              <select
                value={form.method}
                onChange={(e) => setForm((f) => ({ ...f, method: e.target.value as PaymentMethod }))}
              >
                {PAYMENT_METHODS.map((m) => (
                  <option key={m} value={m}>
                    {humanizeEnum(m)}
                  </option>
                ))}
              </select>
            </label>
            <label className="field">
              Session id
              <input
                value={form.sessionId}
                onChange={(e) => setForm((f) => ({ ...f, sessionId: e.target.value }))}
              />
            </label>
            <label className="field">
              Fee id
              <input
                value={form.feeId}
                onChange={(e) => setForm((f) => ({ ...f, feeId: e.target.value }))}
              />
            </label>
            <label className="field field--full">
              Notes
              <textarea
                rows={2}
                value={form.notes}
                onChange={(e) => setForm((f) => ({ ...f, notes: e.target.value }))}
              />
            </label>
          </div>
          {formError && <div className="state state--error">{formError}</div>}
          <div className="modal__actions">
            <button type="button" className="btn btn--ghost" onClick={() => setModalOpen(false)}>
              Cancel
            </button>
            <button type="submit" className="btn btn--primary" disabled={saving}>
              {saving ? "Saving…" : "Save"}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
