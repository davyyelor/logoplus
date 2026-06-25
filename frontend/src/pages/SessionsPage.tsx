import { useState } from "react";
import type { FormEvent } from "react";
import { Modal } from "../components/Modal";
import { StateView } from "../components/StateView";
import { useAsync } from "../hooks/useAsync";
import { patientService } from "../services/patientService";
import { sessionService, type SessionFilters } from "../services/sessionService";
import {
  SESSION_TYPES,
  type SessionType,
  type TherapySession,
  type TherapySessionRequest,
} from "../types/api";
import { formatDate, humanizeEnum, todayIso } from "../utils/format";

interface FormState {
  patientId: string;
  therapistId: string;
  sessionDate: string;
  durationMinutes: string;
  sessionType: SessionType;
  summary: string;
  activitiesPerformed: string;
  patientResponse: string;
  observations: string;
  homework: string;
  nextSteps: string;
}

const EMPTY_FORM: FormState = {
  patientId: "",
  therapistId: "",
  sessionDate: todayIso(),
  durationMinutes: "45",
  sessionType: "THERAPY",
  summary: "",
  activitiesPerformed: "",
  patientResponse: "",
  observations: "",
  homework: "",
  nextSteps: "",
};

export function SessionsPage() {
  const [filters, setFilters] = useState<SessionFilters>({});
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<TherapySession | null>(null);
  const [form, setForm] = useState<FormState>(EMPTY_FORM);
  const [formError, setFormError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const { data, loading, error, reload } = useAsync(() => sessionService.list(filters), [filters]);
  const { data: patients } = useAsync(() => patientService.list({ status: "ACTIVE" }), []);

  const patientName = (id: string) => patients?.find((p) => p.id === id)?.fullName ?? id;

  function openCreate() {
    setEditing(null);
    setForm(EMPTY_FORM);
    setFormError(null);
    setModalOpen(true);
  }

  function openEdit(session: TherapySession) {
    setEditing(session);
    setForm({
      patientId: session.patientId,
      therapistId: session.therapistId ?? "",
      sessionDate: session.sessionDate,
      durationMinutes: session.durationMinutes != null ? String(session.durationMinutes) : "",
      sessionType: session.sessionType,
      summary: session.summary ?? "",
      activitiesPerformed: session.activitiesPerformed ?? "",
      patientResponse: session.patientResponse ?? "",
      observations: session.observations ?? "",
      homework: session.homework ?? "",
      nextSteps: session.nextSteps ?? "",
    });
    setFormError(null);
    setModalOpen(true);
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    if (!form.patientId || !form.sessionDate) {
      setFormError("Paciente y fecha son obligatorios");
      return;
    }
    const duration = form.durationMinutes ? Number(form.durationMinutes) : null;
    const request: TherapySessionRequest = {
      patientId: form.patientId,
      therapistId: form.therapistId.trim() || null,
      sessionDate: form.sessionDate,
      durationMinutes: duration,
      sessionType: form.sessionType,
      summary: form.summary.trim() || null,
      activitiesPerformed: form.activitiesPerformed.trim() || null,
      patientResponse: form.patientResponse.trim() || null,
      observations: form.observations.trim() || null,
      homework: form.homework.trim() || null,
      nextSteps: form.nextSteps.trim() || null,
    };
    setSaving(true);
    setFormError(null);
    try {
      if (editing) {
        await sessionService.update(editing.id, request);
      } else {
        await sessionService.create(request);
      }
      setModalOpen(false);
      reload();
    } catch (err) {
      setFormError(err instanceof Error ? err.message : "No se pudo guardar");
    } finally {
      setSaving(false);
    }
  }

  return (
    <div>
      <div className="page-header">
        <h1>Sesiones terapéuticas</h1>
        <button className="btn btn--primary" onClick={openCreate}>
          Nueva sesión
        </button>
      </div>

      <div className="card">
        <div className="filters">
          <label className="field">
            Paciente
            <select
              value={filters.patientId ?? ""}
              onChange={(e) => setFilters((f) => ({ ...f, patientId: e.target.value || undefined }))}
            >
              <option value="">Todos</option>
              {patients?.map((p) => (
                <option key={p.id} value={p.id}>
                  {p.fullName}
                </option>
              ))}
            </select>
          </label>
          <label className="field">
            Desde
            <input
              type="date"
              value={filters.fromDate ?? ""}
              onChange={(e) => setFilters((f) => ({ ...f, fromDate: e.target.value || undefined }))}
            />
          </label>
          <label className="field">
            Hasta
            <input
              type="date"
              value={filters.toDate ?? ""}
              onChange={(e) => setFilters((f) => ({ ...f, toDate: e.target.value || undefined }))}
            />
          </label>
          <button className="btn btn--ghost" onClick={() => setFilters({})}>
            Limpiar
          </button>
        </div>
      </div>

      <StateView
        loading={loading}
        error={error}
        isEmpty={!data || data.length === 0}
        emptyMessage="No hay sesiones."
      >
        <table>
          <thead>
            <tr>
              <th>Fecha</th>
              <th>Paciente</th>
              <th>Tipo</th>
              <th>Duración</th>
              <th>Resumen</th>
              <th />
            </tr>
          </thead>
          <tbody>
            {data?.map((session) => (
              <tr key={session.id}>
                <td>{formatDate(session.sessionDate)}</td>
                <td>{patientName(session.patientId)}</td>
                <td>{humanizeEnum(session.sessionType)}</td>
                <td>{session.durationMinutes != null ? `${session.durationMinutes} min` : "—"}</td>
                <td className="text-muted">{session.summary ?? "—"}</td>
                <td>
                  <button className="btn btn--sm" onClick={() => openEdit(session)}>
                    Editar
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </StateView>

      <Modal
        open={modalOpen}
        title={editing ? "Editar sesión" : "Nueva sesión"}
        onClose={() => setModalOpen(false)}
      >
        <form onSubmit={handleSubmit}>
          {formError && <div className="state state--error">{formError}</div>}
          <div className="form-grid">
            <label className="field">
              Paciente *
              <select
                value={form.patientId}
                onChange={(e) => setForm((f) => ({ ...f, patientId: e.target.value }))}
                required
              >
                <option value="">Seleccionar…</option>
                {patients?.map((p) => (
                  <option key={p.id} value={p.id}>
                    {p.fullName}
                  </option>
                ))}
              </select>
            </label>
            <label className="field">
              Fecha *
              <input
                type="date"
                value={form.sessionDate}
                onChange={(e) => setForm((f) => ({ ...f, sessionDate: e.target.value }))}
                required
              />
            </label>
            <label className="field">
              Tipo
              <select
                value={form.sessionType}
                onChange={(e) => setForm((f) => ({ ...f, sessionType: e.target.value as SessionType }))}
              >
                {SESSION_TYPES.map((t) => (
                  <option key={t} value={t}>
                    {humanizeEnum(t)}
                  </option>
                ))}
              </select>
            </label>
            <label className="field">
              Duración (min)
              <input
                type="number"
                min="0"
                value={form.durationMinutes}
                onChange={(e) => setForm((f) => ({ ...f, durationMinutes: e.target.value }))}
              />
            </label>
            <label className="field field--full">
              Resumen
              <textarea
                value={form.summary}
                onChange={(e) => setForm((f) => ({ ...f, summary: e.target.value }))}
                rows={2}
              />
            </label>
            <label className="field field--full">
              Actividades realizadas
              <textarea
                value={form.activitiesPerformed}
                onChange={(e) => setForm((f) => ({ ...f, activitiesPerformed: e.target.value }))}
                rows={2}
              />
            </label>
            <label className="field field--full">
              Respuesta del paciente
              <textarea
                value={form.patientResponse}
                onChange={(e) => setForm((f) => ({ ...f, patientResponse: e.target.value }))}
                rows={2}
              />
            </label>
            <label className="field field--full">
              Tareas para casa
              <textarea
                value={form.homework}
                onChange={(e) => setForm((f) => ({ ...f, homework: e.target.value }))}
                rows={2}
              />
            </label>
            <label className="field field--full">
              Próximos pasos
              <textarea
                value={form.nextSteps}
                onChange={(e) => setForm((f) => ({ ...f, nextSteps: e.target.value }))}
                rows={2}
              />
            </label>
          </div>
          <div className="modal__actions">
            <button type="button" className="btn btn--ghost" onClick={() => setModalOpen(false)}>
              Cancelar
            </button>
            <button type="submit" className="btn btn--primary" disabled={saving}>
              {saving ? "Guardando…" : "Guardar"}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
