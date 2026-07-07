import { useState } from "react";
import type { FormEvent } from "react";
import { Link } from "react-router-dom";
import { Modal } from "../components/Modal";
import { StateView } from "../components/StateView";
import { StatusBadge } from "../components/StatusBadge";
import { useAsync } from "../hooks/useAsync";
import { patientService, type PatientFilters } from "../services/patientService";
import { centerService } from "../services/centerService";
import {
  GENDERS,
  PATIENT_STATUSES,
  type Gender,
  type Patient,
  type PatientRequest,
  type PatientStatus,
} from "../types/api";
import { formatDate, humanizeEnum } from "../utils/format";

interface FormState {
  firstName: string;
  lastName: string;
  birthDate: string;
  gender: Gender;
  mainTherapistId: string;
  schoolName: string;
  referralSource: string;
  reasonForConsultation: string;
  relevantNotes: string;
  centerId: string;
}

const EMPTY_FORM: FormState = {
  firstName: "",
  lastName: "",
  birthDate: "",
  gender: "UNSPECIFIED",
  mainTherapistId: "",
  schoolName: "",
  referralSource: "",
  reasonForConsultation: "",
  relevantNotes: "",
  centerId: "",
};

export function PatientsPage() {
  const [filters, setFilters] = useState<PatientFilters>({});
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<Patient | null>(null);
  const [form, setForm] = useState<FormState>(EMPTY_FORM);
  const [formError, setFormError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const { data, loading, error, reload } = useAsync(() => patientService.list(filters), [filters]);
  const { data: centers } = useAsync(() => centerService.list(), []);

  function openCreate() {
    setEditing(null);
    setForm(EMPTY_FORM);
    setFormError(null);
    setModalOpen(true);
  }

  function openEdit(patient: Patient) {
    setEditing(patient);
    setForm({
      firstName: patient.firstName,
      lastName: patient.lastName,
      birthDate: patient.birthDate ?? "",
      gender: patient.gender,
      mainTherapistId: patient.mainTherapistId ?? "",
      schoolName: patient.schoolName ?? "",
      referralSource: patient.referralSource ?? "",
      reasonForConsultation: patient.reasonForConsultation ?? "",
      relevantNotes: patient.relevantNotes ?? "",
      centerId: patient.centerId ?? "",
    });
    setFormError(null);
    setModalOpen(true);
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    if (!form.firstName.trim() || !form.lastName.trim()) {
      setFormError("Nombre y apellidos son obligatorios");
      return;
    }
    const request: PatientRequest = {
      firstName: form.firstName.trim(),
      lastName: form.lastName.trim(),
      birthDate: form.birthDate || null,
      gender: form.gender,
      mainTherapistId: form.mainTherapistId.trim() || null,
      schoolName: form.schoolName.trim() || null,
      referralSource: form.referralSource.trim() || null,
      reasonForConsultation: form.reasonForConsultation.trim() || null,
      relevantNotes: form.relevantNotes.trim() || null,
      centerId: form.centerId || null,
    };
    setSaving(true);
    setFormError(null);
    try {
      if (editing) {
        await patientService.update(editing.id, request);
      } else {
        await patientService.create(request);
      }
      setModalOpen(false);
      reload();
    } catch (err) {
      setFormError(err instanceof Error ? err.message : "No se pudo guardar");
    } finally {
      setSaving(false);
    }
  }

  async function changeStatus(patient: Patient, status: PatientStatus) {
    try {
      await patientService.setStatus(patient.id, status);
      reload();
    } catch (err) {
      globalThis.alert(err instanceof Error ? err.message : "Acción fallida");
    }
  }

  return (
    <div>
      <div className="page-header">
        <h1>Pacientes</h1>
        <button className="btn btn--primary" onClick={openCreate}>
          Nuevo paciente
        </button>
      </div>

      <div className="card">
        <div className="filters">
          <label className="field">
            Buscar
            <input
              value={filters.search ?? ""}
              onChange={(e) => setFilters((f) => ({ ...f, search: e.target.value || undefined }))}
              placeholder="nombre o apellidos"
            />
          </label>
          <label className="field">
            Estado
            <select
              value={filters.status ?? ""}
              onChange={(e) =>
                setFilters((f) => ({ ...f, status: (e.target.value as PatientStatus) || undefined }))
              }
            >
              <option value="">Todos</option>
              {PATIENT_STATUSES.map((s) => (
                <option key={s} value={s}>
                  {humanizeEnum(s)}
                </option>
              ))}
            </select>
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
        emptyMessage="No hay pacientes."
      >
        <table>
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Nacimiento</th>
              <th>Estado</th>
              <th>Motivo</th>
              <th />
            </tr>
          </thead>
          <tbody>
            {data?.map((patient) => (
              <tr key={patient.id}>
                <td>
                  <Link to={`/patients/${patient.id}`}>{patient.fullName}</Link>
                </td>
                <td>{formatDate(patient.birthDate)}</td>
                <td>
                  <StatusBadge status={patient.status} />
                </td>
                <td className="text-muted">{patient.reasonForConsultation ?? "—"}</td>
                <td>
                  <div className="btn-row">
                    <button className="btn btn--sm" onClick={() => openEdit(patient)}>
                      Editar
                    </button>
                    {patient.status === "ACTIVE" ? (
                      <button className="btn btn--sm" onClick={() => changeStatus(patient, "DISCHARGED")}>
                        Dar de alta
                      </button>
                    ) : (
                      <button className="btn btn--sm" onClick={() => changeStatus(patient, "ACTIVE")}>
                        Reactivar
                      </button>
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
        title={editing ? "Editar paciente" : "Nuevo paciente"}
        onClose={() => setModalOpen(false)}
      >
        <form onSubmit={handleSubmit}>
          {formError && <div className="state state--error">{formError}</div>}
          <div className="form-grid">
            <label className="field">
              Nombre *
              <input
                value={form.firstName}
                onChange={(e) => setForm((f) => ({ ...f, firstName: e.target.value }))}
                required
              />
            </label>
            <label className="field">
              Apellidos *
              <input
                value={form.lastName}
                onChange={(e) => setForm((f) => ({ ...f, lastName: e.target.value }))}
                required
              />
            </label>
            <label className="field">
              Fecha de nacimiento
              <input
                type="date"
                value={form.birthDate}
                onChange={(e) => setForm((f) => ({ ...f, birthDate: e.target.value }))}
              />
            </label>
            <label className="field">
              Sexo
              <select
                value={form.gender}
                onChange={(e) => setForm((f) => ({ ...f, gender: e.target.value as Gender }))}
              >
                {GENDERS.map((g) => (
                  <option key={g} value={g}>
                    {humanizeEnum(g)}
                  </option>
                ))}
              </select>
            </label>
            <label className="field">
              Terapeuta principal (id)
              <input
                value={form.mainTherapistId}
                onChange={(e) => setForm((f) => ({ ...f, mainTherapistId: e.target.value }))}
                placeholder="userId"
              />
            </label>
            <label className="field">
              Centro escolar
              <input
                value={form.schoolName}
                onChange={(e) => setForm((f) => ({ ...f, schoolName: e.target.value }))}
              />
            </label>
            <label className="field">
              Derivado por
              <input
                value={form.referralSource}
                onChange={(e) => setForm((f) => ({ ...f, referralSource: e.target.value }))}
              />
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
              Motivo de consulta
              <input
                value={form.reasonForConsultation}
                onChange={(e) => setForm((f) => ({ ...f, reasonForConsultation: e.target.value }))}
              />
            </label>
            <label className="field field--full">
              Notas relevantes
              <textarea
                value={form.relevantNotes}
                onChange={(e) => setForm((f) => ({ ...f, relevantNotes: e.target.value }))}
                rows={3}
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
