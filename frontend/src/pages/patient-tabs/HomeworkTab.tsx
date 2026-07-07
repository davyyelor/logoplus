import { useState } from "react";
import type { FormEvent } from "react";
import { Modal } from "../../components/Modal";
import { StateView } from "../../components/StateView";
import { StatusBadge } from "../../components/StatusBadge";
import { useAsync } from "../../hooks/useAsync";
import { homeworkService } from "../../services/homeworkService";
import {
  HOMEWORK_STATUSES,
  type Homework,
  type HomeworkRequest,
  type HomeworkStatus,
} from "../../types/api";
import { formatDate, humanizeEnum } from "../../utils/format";

interface FormState {
  title: string;
  description: string;
  instructions: string;
  dueDate: string;
  visibleToFamily: boolean;
}

const EMPTY_FORM: FormState = {
  title: "",
  description: "",
  instructions: "",
  dueDate: "",
  visibleToFamily: true,
};

export function HomeworkTab({ patientId }: Readonly<{ patientId: string }>) {
  const { data, loading, error, reload } = useAsync(
    () => homeworkService.listForPatient(patientId),
    [patientId],
  );
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<Homework | null>(null);
  const [form, setForm] = useState<FormState>(EMPTY_FORM);
  const [formError, setFormError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  function openCreate() {
    setEditing(null);
    setForm(EMPTY_FORM);
    setFormError(null);
    setModalOpen(true);
  }

  function openEdit(item: Homework) {
    setEditing(item);
    setForm({
      title: item.title,
      description: item.description ?? "",
      instructions: item.instructions ?? "",
      dueDate: item.dueDate ?? "",
      visibleToFamily: item.visibleToFamily,
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
    const request: HomeworkRequest = {
      title: form.title.trim(),
      description: form.description.trim() || null,
      instructions: form.instructions.trim() || null,
      dueDate: form.dueDate || null,
      visibleToFamily: form.visibleToFamily,
    };
    setSaving(true);
    setFormError(null);
    try {
      if (editing) {
        await homeworkService.update(editing.id, request);
      } else {
        await homeworkService.create(patientId, request);
      }
      setModalOpen(false);
      reload();
    } catch (err) {
      setFormError(err instanceof Error ? err.message : "No se pudo guardar");
    } finally {
      setSaving(false);
    }
  }

  async function changeStatus(item: Homework, status: HomeworkStatus) {
    try {
      await homeworkService.setStatus(item.id, status);
      reload();
    } catch (err) {
      globalThis.alert(err instanceof Error ? err.message : "Acción fallida");
    }
  }

  async function remove(item: Homework) {
    if (!globalThis.confirm(`¿Eliminar la tarea "${item.title}"?`)) return;
    try {
      await homeworkService.remove(item.id);
      reload();
    } catch (err) {
      globalThis.alert(err instanceof Error ? err.message : "Acción fallida");
    }
  }

  return (
    <div>
      <div className="page-header">
        <h2>Tareas para casa</h2>
        <button className="btn btn--primary" onClick={openCreate}>
          Nueva tarea
        </button>
      </div>

      <StateView
        loading={loading}
        error={error}
        isEmpty={!data || data.length === 0}
        emptyMessage="Sin tareas asignadas."
      >
        <table>
          <thead>
            <tr>
              <th>Tarea</th>
              <th>Vence</th>
              <th>Estado</th>
              <th>Familia</th>
              <th />
            </tr>
          </thead>
          <tbody>
            {data?.map((item) => (
              <tr key={item.id}>
                <td>{item.title}</td>
                <td>{formatDate(item.dueDate)}</td>
                <td>
                  <StatusBadge status={item.status} />
                </td>
                <td>{item.visibleToFamily ? "Visible" : "Oculta"}</td>
                <td>
                  <div className="btn-row">
                    <button className="btn btn--sm" onClick={() => openEdit(item)}>
                      Editar
                    </button>
                    <select
                      value={item.status}
                      onChange={(e) => changeStatus(item, e.target.value as HomeworkStatus)}
                    >
                      {HOMEWORK_STATUSES.map((s) => (
                        <option key={s} value={s}>
                          {humanizeEnum(s)}
                        </option>
                      ))}
                    </select>
                    <button className="btn btn--sm btn--ghost" onClick={() => remove(item)}>
                      Eliminar
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
        title={editing ? "Editar tarea" : "Nueva tarea"}
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
              Fecha límite
              <input
                type="date"
                value={form.dueDate}
                onChange={(e) => setForm((f) => ({ ...f, dueDate: e.target.value }))}
              />
            </label>
            <label className="field field--check">
              <input
                type="checkbox"
                checked={form.visibleToFamily}
                onChange={(e) => setForm((f) => ({ ...f, visibleToFamily: e.target.checked }))}
              />
              Visible para la familia
            </label>
            <label className="field field--full">
              Descripción
              <textarea
                value={form.description}
                onChange={(e) => setForm((f) => ({ ...f, description: e.target.value }))}
                rows={2}
              />
            </label>
            <label className="field field--full">
              Instrucciones
              <textarea
                value={form.instructions}
                onChange={(e) => setForm((f) => ({ ...f, instructions: e.target.value }))}
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
