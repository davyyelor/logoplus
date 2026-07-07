import { useState } from "react";
import type { FormEvent } from "react";
import { Modal } from "../components/Modal";
import { StateView } from "../components/StateView";
import { StatusBadge } from "../components/StatusBadge";
import { useAsync } from "../hooks/useAsync";
import { appointmentService, type AppointmentFilters } from "../services/appointmentService";
import { patientService } from "../services/patientService";
import { centerService } from "../services/centerService";
import { calendarService } from "../services/integrationService";
import {
  LOCATION_TYPES,
  type Appointment,
  type AppointmentRequest,
  type LocationType,
} from "../types/api";
import { formatDateTime, humanizeEnum, toDateTimeLocal } from "../utils/format";

interface FormState {
  patientId: string;
  therapistId: string;
  title: string;
  startDateTime: string;
  endDateTime: string;
  locationType: LocationType;
  notes: string;
  centerId: string;
}

const EMPTY_FORM: FormState = {
  patientId: "",
  therapistId: "",
  title: "",
  startDateTime: "",
  endDateTime: "",
  locationType: "IN_PERSON",
  notes: "",
  centerId: "",
};

export function AppointmentsPage() {
  const [filters, setFilters] = useState<AppointmentFilters>({});
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<Appointment | null>(null);
  const [form, setForm] = useState<FormState>(EMPTY_FORM);
  const [formError, setFormError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const { data, loading, error, reload } = useAsync(
    () => appointmentService.list(filters),
    [filters],
  );
  const { data: patients } = useAsync(() => patientService.list({ status: "ACTIVE" }), []);
  const { data: centers } = useAsync(() => centerService.list(), []);

  const patientName = (id: string) => patients?.find((p) => p.id === id)?.fullName ?? id;

  function openCreate() {
    setEditing(null);
    setForm(EMPTY_FORM);
    setFormError(null);
    setModalOpen(true);
  }

  function openEdit(appt: Appointment) {
    setEditing(appt);
    setForm({
      patientId: appt.patientId,
      therapistId: appt.therapistId ?? "",
      title: appt.title ?? "",
      startDateTime: toDateTimeLocal(appt.startDateTime),
      endDateTime: toDateTimeLocal(appt.endDateTime),
      locationType: appt.locationType,
      notes: appt.notes ?? "",
      centerId: appt.centerId ?? "",
    });
    setFormError(null);
    setModalOpen(true);
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    if (!form.patientId || !form.startDateTime || !form.endDateTime) {
      setFormError("Paciente, inicio y fin son obligatorios");
      return;
    }
    const request: AppointmentRequest = {
      patientId: form.patientId,
      therapistId: form.therapistId.trim() || null,
      title: form.title.trim() || null,
      startDateTime: new Date(form.startDateTime).toISOString(),
      endDateTime: new Date(form.endDateTime).toISOString(),
      locationType: form.locationType,
      notes: form.notes.trim() || null,
      centerId: form.centerId || null,
    };
    setSaving(true);
    setFormError(null);
    try {
      if (editing) {
        await appointmentService.update(editing.id, request);
      } else {
        await appointmentService.create(request);
      }
      setModalOpen(false);
      reload();
    } catch (err) {
      setFormError(err instanceof Error ? err.message : "No se pudo guardar");
    } finally {
      setSaving(false);
    }
  }

  async function runAction(action: () => Promise<unknown>) {
    try {
      await action();
      reload();
    } catch (err) {
      globalThis.alert(err instanceof Error ? err.message : "Acción fallida");
    }
  }

  async function exportIcs(id: string) {
    try {
      await calendarService.exportIcs(id);
    } catch (err) {
      globalThis.alert(err instanceof Error ? err.message : "No se pudo exportar el .ics");
    }
  }

  return (
    <div>
      <div className="page-header">
        <h1>Agenda</h1>
        <button className="btn btn--primary" onClick={openCreate}>
          Nueva cita
        </button>
      </div>

      <div className="card">
        <div className="filters">
          <label className="field">
            Desde
            <input
              type="date"
              value={filters.from ?? ""}
              onChange={(e) => setFilters((f) => ({ ...f, from: e.target.value || undefined }))}
            />
          </label>
          <label className="field">
            Hasta
            <input
              type="date"
              value={filters.to ?? ""}
              onChange={(e) => setFilters((f) => ({ ...f, to: e.target.value || undefined }))}
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
        emptyMessage="No hay citas."
      >
        <table>
          <thead>
            <tr>
              <th>Inicio</th>
              <th>Fin</th>
              <th>Paciente</th>
              <th>Modalidad</th>
              <th>Estado</th>
              <th />
            </tr>
          </thead>
          <tbody>
            {data?.map((appt) => (
              <tr key={appt.id}>
                <td>{formatDateTime(appt.startDateTime)}</td>
                <td>{formatDateTime(appt.endDateTime)}</td>
                <td>{patientName(appt.patientId)}</td>
                <td>{humanizeEnum(appt.locationType)}</td>
                <td>
                  <StatusBadge status={appt.status} />
                </td>
                <td>
                  <div className="btn-row">
                    {appt.status === "SCHEDULED" && (
                      <>
                        <button className="btn btn--sm" onClick={() => openEdit(appt)}>
                          Editar
                        </button>
                        <button className="btn btn--sm" onClick={() => runAction(() => appointmentService.complete(appt.id))}>
                          Completar
                        </button>
                        <button className="btn btn--sm" onClick={() => runAction(() => appointmentService.noShow(appt.id))}>
                          No asistió
                        </button>
                        <button className="btn btn--sm" onClick={() => runAction(() => appointmentService.cancel(appt.id))}>
                          Cancelar
                        </button>
                      </>
                    )}
                    <button className="btn btn--sm" onClick={() => exportIcs(appt.id)}>
                      .ics
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
        title={editing ? "Editar cita" : "Nueva cita"}
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
              Terapeuta (id)
              <input
                value={form.therapistId}
                onChange={(e) => setForm((f) => ({ ...f, therapistId: e.target.value }))}
                placeholder="userId"
              />
            </label>
            <label className="field">
              Inicio *
              <input
                type="datetime-local"
                value={form.startDateTime}
                onChange={(e) => setForm((f) => ({ ...f, startDateTime: e.target.value }))}
                required
              />
            </label>
            <label className="field">
              Fin *
              <input
                type="datetime-local"
                value={form.endDateTime}
                onChange={(e) => setForm((f) => ({ ...f, endDateTime: e.target.value }))}
                required
              />
            </label>
            <label className="field">
              Modalidad
              <select
                value={form.locationType}
                onChange={(e) => setForm((f) => ({ ...f, locationType: e.target.value as LocationType }))}
              >
                {LOCATION_TYPES.map((l) => (
                  <option key={l} value={l}>
                    {humanizeEnum(l)}
                  </option>
                ))}
              </select>
            </label>
            <label className="field">
              Centro
              <select
                value={form.centerId}
                onChange={(e) => setForm((f) => ({ ...f, centerId: e.target.value }))}
              >
                <option value="">Sin centro</option>
                {centers?.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.name}
                  </option>
                ))}
              </select>
            </label>
            <label className="field field--full">
              Título / motivo
              <input
                value={form.title}
                onChange={(e) => setForm((f) => ({ ...f, title: e.target.value }))}
              />
            </label>
            <label className="field field--full">
              Notas
              <textarea
                value={form.notes}
                onChange={(e) => setForm((f) => ({ ...f, notes: e.target.value }))}
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
