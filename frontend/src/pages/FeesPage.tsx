import { useState } from "react";
import { Modal } from "../components/Modal";
import { StateView } from "../components/StateView";
import { useAsync } from "../hooks/useAsync";
import { feeService, type FeeFilters } from "../services/feeService";
import {
  RECURRENCE_TYPES,
  type Fee,
  type FeeRequest,
  type RecurrenceType,
} from "../types/billing";
import { formatCurrency, formatDate, humanizeEnum, todayIso } from "../utils/format";

interface FormState {
  patientId: string;
  name: string;
  amount: string;
  currency: string;
  recurrenceType: RecurrenceType;
  startDate: string;
  endDate: string;
  notes: string;
}

const EMPTY_FORM: FormState = {
  patientId: "",
  name: "",
  amount: "",
  currency: "EUR",
  recurrenceType: "MONTHLY",
  startDate: todayIso(),
  endDate: "",
  notes: "",
};

export function FeesPage() {
  const [filters, setFilters] = useState<FeeFilters>({});
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<Fee | null>(null);
  const [form, setForm] = useState<FormState>(EMPTY_FORM);
  const [formError, setFormError] = useState<string | null>(null);
  const [toast, setToast] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const { data, loading, error, reload } = useAsync(() => feeService.list(filters), [filters]);

  function openCreate() {
    setEditing(null);
    setForm(EMPTY_FORM);
    setFormError(null);
    setModalOpen(true);
  }

  function openEdit(fee: Fee) {
    setEditing(fee);
    setForm({
      patientId: fee.patientId,
      name: fee.name,
      amount: String(fee.amount),
      currency: fee.currency,
      recurrenceType: fee.recurrenceType,
      startDate: fee.startDate,
      endDate: fee.endDate ?? "",
      notes: fee.notes ?? "",
    });
    setFormError(null);
    setModalOpen(true);
  }

  async function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    setFormError(null);
    const amount = Number(form.amount);
    if (!form.patientId.trim() || !form.name.trim()) {
      setFormError("Patient and name are required");
      return;
    }
    if (!Number.isFinite(amount) || amount <= 0) {
      setFormError("Amount must be positive");
      return;
    }
    const request: FeeRequest = {
      patientId: form.patientId.trim(),
      name: form.name.trim(),
      amount,
      currency: form.currency || "EUR",
      recurrenceType: form.recurrenceType,
      startDate: form.startDate,
      endDate: form.endDate || null,
      notes: form.notes.trim() || null,
    };
    setSaving(true);
    try {
      if (editing) {
        await feeService.update(editing.id, request);
        setToast("Fee updated");
      } else {
        await feeService.create(request);
        setToast("Fee created");
      }
      setModalOpen(false);
      reload();
    } catch (err) {
      setFormError(err instanceof Error ? err.message : "Save failed");
    } finally {
      setSaving(false);
    }
  }

  async function toggleActive(fee: Fee) {
    try {
      if (fee.active) {
        await feeService.deactivate(fee.id);
        setToast("Fee deactivated");
      } else {
        await feeService.activate(fee.id);
        setToast("Fee activated");
      }
      reload();
    } catch (err) {
      window.alert(err instanceof Error ? err.message : "Action failed");
    }
  }

  return (
    <div>
      <div className="page-header">
        <h1>Fees</h1>
        <button className="btn btn--primary" onClick={openCreate}>
          New fee
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
            Active
            <select
              value={filters.active === undefined ? "" : String(filters.active)}
              onChange={(e) =>
                setFilters((f) => ({
                  ...f,
                  active: e.target.value === "" ? undefined : e.target.value === "true",
                }))
              }
            >
              <option value="">All</option>
              <option value="true">Active</option>
              <option value="false">Inactive</option>
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
        emptyMessage="No fees found."
      >
        <table>
          <thead>
            <tr>
              <th>Name</th>
              <th>Patient</th>
              <th>Amount</th>
              <th>Recurrence</th>
              <th>Start</th>
              <th>End</th>
              <th>Active</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {data?.map((fee) => (
              <tr key={fee.id}>
                <td>{fee.name}</td>
                <td>{fee.patientId}</td>
                <td className="amount">{formatCurrency(fee.amount, fee.currency)}</td>
                <td>{humanizeEnum(fee.recurrenceType)}</td>
                <td>{formatDate(fee.startDate)}</td>
                <td>{formatDate(fee.endDate)}</td>
                <td>{fee.active ? "Yes" : "No"}</td>
                <td>
                  <div className="btn-row">
                    <button className="btn btn--sm" onClick={() => openEdit(fee)}>
                      Edit
                    </button>
                    <button className="btn btn--sm" onClick={() => toggleActive(fee)}>
                      {fee.active ? "Deactivate" : "Activate"}
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </StateView>

      <Modal
        open={modalOpen}
        title={editing ? "Edit fee" : "New fee"}
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
              Name *
              <input
                value={form.name}
                onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))}
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
              Recurrence *
              <select
                value={form.recurrenceType}
                onChange={(e) =>
                  setForm((f) => ({ ...f, recurrenceType: e.target.value as RecurrenceType }))
                }
              >
                {RECURRENCE_TYPES.map((r) => (
                  <option key={r} value={r}>
                    {humanizeEnum(r)}
                  </option>
                ))}
              </select>
            </label>
            <label className="field">
              Start date *
              <input
                type="date"
                value={form.startDate}
                onChange={(e) => setForm((f) => ({ ...f, startDate: e.target.value }))}
                required
              />
            </label>
            <label className="field">
              End date
              <input
                type="date"
                value={form.endDate}
                onChange={(e) => setForm((f) => ({ ...f, endDate: e.target.value }))}
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
