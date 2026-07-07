import { useState } from "react";
import type { FormEvent } from "react";
import { Modal } from "../components/Modal";
import { StateView } from "../components/StateView";
import { useAsync } from "../hooks/useAsync";
import { questionnaireService } from "../services/questionnaireService";
import {
  QUESTION_TYPES,
  QUESTIONNAIRE_TARGET_ROLES,
  type QuestionRequest,
  type QuestionType,
  type QuestionnaireTargetRole,
  type QuestionnaireTemplate,
} from "../types/api";
import { humanizeEnum } from "../utils/format";

interface QuestionRow {
  text: string;
  type: QuestionType;
  required: boolean;
}

interface FormState {
  name: string;
  description: string;
  targetRole: QuestionnaireTargetRole;
  active: boolean;
  questions: QuestionRow[];
}

const EMPTY_QUESTION: QuestionRow = { text: "", type: "TEXT", required: false };

const EMPTY_FORM: FormState = {
  name: "",
  description: "",
  targetRole: "FAMILY",
  active: true,
  questions: [{ ...EMPTY_QUESTION }],
};

export function QuestionnaireTemplatesPage() {
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<QuestionnaireTemplate | null>(null);
  const [form, setForm] = useState<FormState>(EMPTY_FORM);
  const [formError, setFormError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const { data, loading, error, reload } = useAsync(() => questionnaireService.listTemplates(), []);

  function openCreate() {
    setEditing(null);
    setForm({ ...EMPTY_FORM, questions: [{ ...EMPTY_QUESTION }] });
    setFormError(null);
    setModalOpen(true);
  }

  function openEdit(template: QuestionnaireTemplate) {
    setEditing(template);
    setForm({
      name: template.name,
      description: template.description ?? "",
      targetRole: template.targetRole,
      active: template.active,
      questions:
        template.questions.length > 0
          ? template.questions.map((q) => ({ text: q.text, type: q.type, required: q.required }))
          : [{ ...EMPTY_QUESTION }],
    });
    setFormError(null);
    setModalOpen(true);
  }

  function updateQuestion(index: number, patch: Partial<QuestionRow>) {
    setForm((f) => ({
      ...f,
      questions: f.questions.map((q, i) => (i === index ? { ...q, ...patch } : q)),
    }));
  }

  function addQuestion() {
    setForm((f) => ({ ...f, questions: [...f.questions, { ...EMPTY_QUESTION }] }));
  }

  function removeQuestion(index: number) {
    setForm((f) => ({ ...f, questions: f.questions.filter((_, i) => i !== index) }));
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    if (!form.name.trim()) {
      setFormError("El nombre es obligatorio");
      return;
    }
    const questions: QuestionRequest[] = form.questions
      .filter((q) => q.text.trim())
      .map((q, i) => ({ text: q.text.trim(), type: q.type, required: q.required, position: i }));
    if (questions.length === 0) {
      setFormError("Añade al menos una pregunta");
      return;
    }
    setSaving(true);
    setFormError(null);
    try {
      const request = {
        name: form.name.trim(),
        description: form.description.trim() || null,
        targetRole: form.targetRole,
        active: form.active,
        questions,
      };
      if (editing) {
        await questionnaireService.updateTemplate(editing.id, request);
      } else {
        await questionnaireService.createTemplate(request);
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
        <h1>Cuestionarios</h1>
        <button className="btn btn--primary" onClick={openCreate}>
          Nuevo cuestionario
        </button>
      </div>

      <StateView
        loading={loading}
        error={error}
        isEmpty={!data || data.length === 0}
        emptyMessage="No hay cuestionarios."
      >
        <table>
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Dirigido a</th>
              <th>Preguntas</th>
              <th>Activo</th>
              <th />
            </tr>
          </thead>
          <tbody>
            {data?.map((template) => (
              <tr key={template.id}>
                <td>{template.name}</td>
                <td>{humanizeEnum(template.targetRole)}</td>
                <td>{template.questions.length}</td>
                <td>{template.active ? "Sí" : "No"}</td>
                <td>
                  <button className="btn btn--sm" onClick={() => openEdit(template)}>
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
        title={editing ? "Editar cuestionario" : "Nuevo cuestionario"}
        onClose={() => setModalOpen(false)}
      >
        <form onSubmit={handleSubmit}>
          {formError && <div className="state state--error">{formError}</div>}
          <div className="form-grid">
            <label className="field">
              Nombre *
              <input
                value={form.name}
                onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))}
                required
              />
            </label>
            <label className="field">
              Dirigido a
              <select
                value={form.targetRole}
                onChange={(e) =>
                  setForm((f) => ({ ...f, targetRole: e.target.value as QuestionnaireTargetRole }))
                }
              >
                {QUESTIONNAIRE_TARGET_ROLES.map((r) => (
                  <option key={r} value={r}>
                    {humanizeEnum(r)}
                  </option>
                ))}
              </select>
            </label>
            <label className="field field--full">
              Descripción
              <textarea
                value={form.description}
                onChange={(e) => setForm((f) => ({ ...f, description: e.target.value }))}
                rows={2}
              />
            </label>
            <label className="field field--check">
              <input
                type="checkbox"
                checked={form.active}
                onChange={(e) => setForm((f) => ({ ...f, active: e.target.checked }))}
              />
              Activo
            </label>
          </div>

          <h3>Preguntas</h3>
          {form.questions.map((q, index) => (
            <div key={index} className="form-grid">
              <label className="field field--full">
                Pregunta {index + 1}
                <input
                  value={q.text}
                  onChange={(e) => updateQuestion(index, { text: e.target.value })}
                />
              </label>
              <label className="field">
                Tipo
                <select
                  value={q.type}
                  onChange={(e) => updateQuestion(index, { type: e.target.value as QuestionType })}
                >
                  {QUESTION_TYPES.map((t) => (
                    <option key={t} value={t}>
                      {humanizeEnum(t)}
                    </option>
                  ))}
                </select>
              </label>
              <label className="field field--check">
                <input
                  type="checkbox"
                  checked={q.required}
                  onChange={(e) => updateQuestion(index, { required: e.target.checked })}
                />
                Obligatoria
              </label>
              {form.questions.length > 1 && (
                <button
                  type="button"
                  className="btn btn--sm btn--ghost"
                  onClick={() => removeQuestion(index)}
                >
                  Quitar
                </button>
              )}
            </div>
          ))}
          <button type="button" className="btn btn--sm" onClick={addQuestion}>
            Añadir pregunta
          </button>

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
