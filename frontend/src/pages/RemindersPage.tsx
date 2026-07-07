import { useState } from "react";
import { Modal } from "../components/Modal";
import { StateView } from "../components/StateView";
import { StatusBadge } from "../components/StatusBadge";
import { useAuth } from "../context/AuthContext";
import { useAsync } from "../hooks/useAsync";
import { reminderService } from "../services/reminderService";
import type { Reminder, ReminderRequest } from "../types/api";
import { formatDateTime, humanizeEnum } from "../utils/format";

interface FormState {
  title: string;
  message: string;
  remindAt: string;
  patientId: string;
  targetUserId: string;
}

const EMPTY_FORM: FormState = {
  title: "",
  message: "",
  remindAt: "",
  patientId: "",
  targetUserId: "",
};

export function RemindersPage() {
  const { hasRole } = useAuth();
  const canManage = hasRole("CLINIC_ADMIN", "THERAPIST");
  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState<FormState>(EMPTY_FORM);
  const [formError, setFormError] = useState<string | null>(null);
  const [toast, setToast] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const { data, loading, error, reload } = useAsync(() => reminderService.list(), []);

  function openCreate() {
    setForm(EMPTY_FORM);
    setFormError(null);
    setModalOpen(true);
  }

  async function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    setFormError(null);
    if (!form.title.trim() || !form.remindAt) {
      setFormError("El título y la fecha son obligatorios");
      return;
    }
    const request: ReminderRequest = {
      title: form.title.trim(),
      message: form.message.trim() || null,
      remindAt: new Date(form.remindAt).toISOString(),
      patientId: form.patientId.trim() || null,
      targetUserId: form.targetUserId.trim() || null,
    };
    setSaving(true);
    try {
      await reminderService.create(request);
      setToast("Recordatorio creado");
      setModalOpen(false);
      reload();
    } catch (err) {
      setFormError(err instanceof Error ? err.message : "No se pudo guardar");
    } finally {
      setSaving(false);
    }
  }

  async function cancel(reminder: Reminder) {
    try {
      await reminderService.cancel(reminder.id);
      setToast("Recordatorio cancelado");
      reload();
    } catch (err) {
      globalThis.alert(err instanceof Error ? err.message : "La acción falló");
    }
  }

  async function remove(reminder: Reminder) {
    if (!globalThis.confirm("¿Eliminar este recordatorio?")) {
      return;
    }
    try {
      await reminderService.remove(reminder.id);
      setToast("Recordatorio eliminado");
      reload();
    } catch (err) {
      globalThis.alert(err instanceof Error ? err.message : "La acción falló");
    }
  }

  return (
    <div>
      <div className="page-header">
        <h1>Recordatorios</h1>
        {canManage && (
          <button className="btn btn--primary" onClick={openCreate}>
            Nuevo recordatorio
          </button>
        )}
      </div>

      {toast && <div className="toast">{toast}</div>}

      <StateView
        loading={loading}
        error={error}
        isEmpty={!data || data.length === 0}
        emptyMessage="No hay recordatorios."
      >
        <table>
          <thead>
            <tr>
              <th>Título</th>
              <th>Cuándo</th>
              <th>Estado</th>
              <th>Mensaje</th>
              {canManage && <th></th>}
            </tr>
          </thead>
          <tbody>
            {data?.map((reminder) => (
              <tr key={reminder.id}>
                <td>{reminder.title}</td>
                <td>{formatDateTime(reminder.remindAt)}</td>
                <td>
                  <StatusBadge status={humanizeEnum(reminder.status)} />
                </td>
                <td>{reminder.message}</td>
                {canManage && (
                  <td>
                    <div className="btn-row">
                      {reminder.status === "SCHEDULED" && (
                        <button className="btn btn--sm" onClick={() => cancel(reminder)}>
                          Cancelar
                        </button>
                      )}
                      <button className="btn btn--sm" onClick={() => remove(reminder)}>
                        Eliminar
                      </button>
                    </div>
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      </StateView>

      <Modal open={modalOpen} title="Nuevo recordatorio" onClose={() => setModalOpen(false)}>
        <form onSubmit={handleSubmit}>
          <div className="form-grid">
            <label className="field">
              Título *
              <input
                value={form.title}
                onChange={(e) => setForm((f) => ({ ...f, title: e.target.value }))}
                required
              />
            </label>
            <label className="field">
              Fecha y hora *
              <input
                type="datetime-local"
                value={form.remindAt}
                onChange={(e) => setForm((f) => ({ ...f, remindAt: e.target.value }))}
                required
              />
            </label>
            <label className="field">
              Paciente (opcional)
              <input
                value={form.patientId}
                onChange={(e) => setForm((f) => ({ ...f, patientId: e.target.value }))}
                placeholder="patientId"
              />
            </label>
            <label className="field">
              Usuario destinatario (opcional)
              <input
                value={form.targetUserId}
                onChange={(e) => setForm((f) => ({ ...f, targetUserId: e.target.value }))}
                placeholder="userId"
              />
            </label>
            <label className="field field--full">
              Mensaje
              <textarea
                value={form.message}
                onChange={(e) => setForm((f) => ({ ...f, message: e.target.value }))}
                rows={3}
              />
            </label>
          </div>
          {formError && <p className="form-error">{formError}</p>}
          <div className="modal__actions">
            <button type="button" className="btn btn--ghost" onClick={() => setModalOpen(false)}>
              Cancelar
            </button>
            <button type="submit" className="btn btn--primary" disabled={saving}>
              {saving ? "Guardando…" : "Crear"}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
