import { useState } from "react";
import type { FormEvent } from "react";
import { Modal } from "../../components/Modal";
import { StateView } from "../../components/StateView";
import { StatusBadge } from "../../components/StatusBadge";
import { useAsync } from "../../hooks/useAsync";
import { goalService } from "../../services/goalService";
import {
  GOAL_PRIORITIES,
  GOAL_STATUSES,
  type GoalPriority,
  type GoalStatus,
  type TherapyGoal,
  type TherapyGoalRequest,
} from "../../types/api";
import { formatDate, humanizeEnum } from "../../utils/format";

interface FormState {
  area: string;
  title: string;
  description: string;
  status: GoalStatus;
  priority: GoalPriority;
  startDate: string;
  targetDate: string;
}

const EMPTY_FORM: FormState = {
  area: "",
  title: "",
  description: "",
  status: "NOT_STARTED",
  priority: "MEDIUM",
  startDate: "",
  targetDate: "",
};

export function GoalsTab({ patientId }: Readonly<{ patientId: string }>) {
  const { data, loading, error, reload } = useAsync(
    () => goalService.listForPatient(patientId),
    [patientId],
  );
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<TherapyGoal | null>(null);
  const [form, setForm] = useState<FormState>(EMPTY_FORM);
  const [formError, setFormError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  function openCreate() {
    setEditing(null);
    setForm(EMPTY_FORM);
    setFormError(null);
    setModalOpen(true);
  }

  function openEdit(goal: TherapyGoal) {
    setEditing(goal);
    setForm({
      area: goal.area ?? "",
      title: goal.title,
      description: goal.description ?? "",
      status: goal.status,
      priority: goal.priority,
      startDate: goal.startDate ?? "",
      targetDate: goal.targetDate ?? "",
    });
    setFormError(null);
    setModalOpen(true);
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    if (!form.title.trim()) {
      setFormError("El título es obligatorio");
      return;
    }
    const request: TherapyGoalRequest = {
      area: form.area.trim() || null,
      title: form.title.trim(),
      description: form.description.trim() || null,
      status: form.status,
      priority: form.priority,
      startDate: form.startDate || null,
      targetDate: form.targetDate || null,
      achievedDate: form.status === "ACHIEVED" ? new Date().toISOString().slice(0, 10) : null,
    };
    setSaving(true);
    setFormError(null);
    try {
      if (editing) {
        await goalService.update(editing.id, request);
      } else {
        await goalService.create(patientId, request);
      }
      setModalOpen(false);
      reload();
    } catch (err) {
      setFormError(err instanceof Error ? err.message : "No se pudo guardar");
    } finally {
      setSaving(false);
    }
  }

  async function changeStatus(goal: TherapyGoal, status: GoalStatus) {
    try {
      await goalService.setStatus(goal.id, status);
      reload();
    } catch (err) {
      globalThis.alert(err instanceof Error ? err.message : "Acción fallida");
    }
  }

  return (
    <div>
      <div className="page-header">
        <h2>Objetivos terapéuticos</h2>
        <button className="btn btn--primary" onClick={openCreate}>
          Nuevo objetivo
        </button>
      </div>

      <StateView
        loading={loading}
        error={error}
        isEmpty={!data || data.length === 0}
        emptyMessage="Sin objetivos definidos."
      >
        <table>
          <thead>
            <tr>
              <th>Objetivo</th>
              <th>Área</th>
              <th>Prioridad</th>
              <th>Estado</th>
              <th>Objetivo para</th>
              <th />
            </tr>
          </thead>
          <tbody>
            {data?.map((goal) => (
              <tr key={goal.id}>
                <td>{goal.title}</td>
                <td>{goal.area ?? "—"}</td>
                <td>{humanizeEnum(goal.priority)}</td>
                <td>
                  <StatusBadge status={goal.status} />
                </td>
                <td>{formatDate(goal.targetDate)}</td>
                <td>
                  <div className="btn-row">
                    <button className="btn btn--sm" onClick={() => openEdit(goal)}>
                      Editar
                    </button>
                    {goal.status !== "ACHIEVED" && (
                      <button className="btn btn--sm" onClick={() => changeStatus(goal, "ACHIEVED")}>
                        Conseguido
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
        title={editing ? "Editar objetivo" : "Nuevo objetivo"}
        onClose={() => setModalOpen(false)}
      >
        <form onSubmit={handleSubmit}>
          {formError && <div className="state state--error">{formError}</div>}
          <div className="form-grid">
            <label className="field field--full">
              Título *
              <input
                value={form.title}
                onChange={(e) => setForm((f) => ({ ...f, title: e.target.value }))}
                required
              />
            </label>
            <label className="field">
              Área
              <input
                value={form.area}
                onChange={(e) => setForm((f) => ({ ...f, area: e.target.value }))}
                placeholder="p. ej. Lenguaje expresivo"
              />
            </label>
            <label className="field">
              Prioridad
              <select
                value={form.priority}
                onChange={(e) => setForm((f) => ({ ...f, priority: e.target.value as GoalPriority }))}
              >
                {GOAL_PRIORITIES.map((p) => (
                  <option key={p} value={p}>
                    {humanizeEnum(p)}
                  </option>
                ))}
              </select>
            </label>
            <label className="field">
              Estado
              <select
                value={form.status}
                onChange={(e) => setForm((f) => ({ ...f, status: e.target.value as GoalStatus }))}
              >
                {GOAL_STATUSES.map((s) => (
                  <option key={s} value={s}>
                    {humanizeEnum(s)}
                  </option>
                ))}
              </select>
            </label>
            <label className="field">
              Inicio
              <input
                type="date"
                value={form.startDate}
                onChange={(e) => setForm((f) => ({ ...f, startDate: e.target.value }))}
              />
            </label>
            <label className="field">
              Objetivo para
              <input
                type="date"
                value={form.targetDate}
                onChange={(e) => setForm((f) => ({ ...f, targetDate: e.target.value }))}
              />
            </label>
            <label className="field field--full">
              Descripción
              <textarea
                value={form.description}
                onChange={(e) => setForm((f) => ({ ...f, description: e.target.value }))}
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
