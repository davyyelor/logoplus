import { useState } from "react";
import { Modal } from "../components/Modal";
import { StateView } from "../components/StateView";
import { StatusBadge } from "../components/StatusBadge";
import { useAsync } from "../hooks/useAsync";
import {
  sessionBillingService,
  type SessionBillingFilters,
} from "../services/sessionBillingService";
import {
  SESSION_BILLING_STATUSES,
  type SessionBillingRequest,
  type SessionBillingStatus,
} from "../types/billing";
import { formatCurrency, formatDate, humanizeEnum, todayIso } from "../utils/format";

interface FormState {
  patientId: string;
  sessionId: string;
  sessionDate: string;
  amount: string;
  currency: string;
  paidAmount: string;
  notes: string;
}

const EMPTY_FORM: FormState = {
  patientId: "",
  sessionId: "",
  sessionDate: todayIso(),
  amount: "",
  currency: "EUR",
  paidAmount: "0",
  notes: "",
};

export function SessionBillingPage() {
  const [filters, setFilters] = useState<SessionBillingFilters>({});
  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState<FormState>(EMPTY_FORM);
  const [formError, setFormError] = useState<string | null>(null);
  const [toast, setToast] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const { data, loading, error, reload } = useAsync(
    () => sessionBillingService.list(filters),
    [filters],
  );

  function openCreate() {
    setForm(EMPTY_FORM);
    setFormError(null);
    setModalOpen(true);
  }

  async function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    setFormError(null);
    const amount = Number(form.amount);
    const paidAmount = Number(form.paidAmount || "0");
    if (!form.patientId.trim() || !form.sessionId.trim()) {
      setFormError("Patient and session id are required");
      return;
    }
    if (!Number.isFinite(amount) || amount < 0) {
      setFormError("Amount cannot be negative");
      return;
    }
    if (paidAmount > amount) {
      setFormError("Paid amount cannot be greater than amount");
      return;
    }
    const request: SessionBillingRequest = {
      patientId: form.patientId.trim(),
      sessionId: form.sessionId.trim(),
      sessionDate: form.sessionDate,
      amount,
      currency: form.currency || "EUR",
      paidAmount,
      notes: form.notes.trim() || null,
    };
    setSaving(true);
    try {
      await sessionBillingService.create(request);
      setToast("Session billing created");
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
      window.alert(err instanceof Error ? err.message : "Action failed");
    }
  }

  return (
    <div>
      <div className="page-header">
        <h1>Session billing</h1>
        <button className="btn btn--primary" onClick={openCreate}>
          New session billing
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
            Status
            <select
              value={filters.status ?? ""}
              onChange={(e) =>
                setFilters((f) => ({
                  ...f,
                  status: (e.target.value as SessionBillingStatus) || undefined,
                }))
              }
            >
              <option value="">All</option>
              {SESSION_BILLING_STATUSES.map((s) => (
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
        emptyMessage="No session billing records found."
      >
        <table>
          <thead>
            <tr>
              <th>Date</th>
              <th>Patient</th>
              <th>Session</th>
              <th>Amount</th>
              <th>Paid</th>
              <th>Pending</th>
              <th>Status</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {data?.map((session) => (
              <tr key={session.id}>
                <td>{formatDate(session.sessionDate)}</td>
                <td>{session.patientId}</td>
                <td>{session.sessionId}</td>
                <td className="amount">{formatCurrency(session.amount, session.currency)}</td>
                <td className="amount">{formatCurrency(session.paidAmount, session.currency)}</td>
                <td className="amount">{formatCurrency(session.pendingAmount, session.currency)}</td>
                <td>
                  <StatusBadge status={session.status} />
                </td>
                <td>
                  <div className="btn-row">
                    <button
                      className="btn btn--sm"
                      onClick={() =>
                        runAction(
                          () => sessionBillingService.markPaid(session.id),
                          "Marked as paid",
                        )
                      }
                    >
                      Paid
                    </button>
                    <button
                      className="btn btn--sm"
                      onClick={() =>
                        runAction(
                          () => sessionBillingService.markPending(session.id),
                          "Marked as pending",
                        )
                      }
                    >
                      Pending
                    </button>
                    <button
                      className="btn btn--sm"
                      onClick={() =>
                        runAction(
                          () => sessionBillingService.markNoCharge(session.id),
                          "Marked as no charge",
                        )
                      }
                    >
                      No charge
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </StateView>

      <Modal open={modalOpen} title="New session billing" onClose={() => setModalOpen(false)}>
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
              Session id *
              <input
                value={form.sessionId}
                onChange={(e) => setForm((f) => ({ ...f, sessionId: e.target.value }))}
                required
              />
            </label>
            <label className="field">
              Session date *
              <input
                type="date"
                value={form.sessionDate}
                onChange={(e) => setForm((f) => ({ ...f, sessionDate: e.target.value }))}
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
              Paid amount
              <input
                type="number"
                step="0.01"
                min="0"
                value={form.paidAmount}
                onChange={(e) => setForm((f) => ({ ...f, paidAmount: e.target.value }))}
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
