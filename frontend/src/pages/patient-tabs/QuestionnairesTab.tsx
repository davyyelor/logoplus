import { useState } from "react";
import type { FormEvent } from "react";
import { Modal } from "../../components/Modal";
import { StateView } from "../../components/StateView";
import { StatusBadge } from "../../components/StatusBadge";
import { useAsync } from "../../hooks/useAsync";
import { questionnaireService } from "../../services/questionnaireService";
import type {
  QuestionnaireAssignment,
  QuestionnaireResponseDetail,
} from "../../types/api";
import { formatDate, formatDateTime } from "../../utils/format";

export function QuestionnairesTab({ patientId }: Readonly<{ patientId: string }>) {
  const assignments = useAsync(
    () => questionnaireService.listAssignments(patientId),
    [patientId],
  );
  const templates = useAsync(() => questionnaireService.listTemplates(), []);

  const [assignModalOpen, setAssignModalOpen] = useState(false);
  const [templateId, setTemplateId] = useState("");
  const [dueDate, setDueDate] = useState("");
  const [saving, setSaving] = useState(false);
  const [formError, setFormError] = useState<string | null>(null);

  const [responses, setResponses] = useState<QuestionnaireResponseDetail[] | null>(null);
  const [responsesTitle, setResponsesTitle] = useState("");

  async function handleAssign(event: FormEvent) {
    event.preventDefault();
    if (!templateId) {
      setFormError("Selecciona un cuestionario");
      return;
    }
    setSaving(true);
    setFormError(null);
    try {
      await questionnaireService.assign(patientId, {
        templateId,
        dueDate: dueDate || null,
      });
      setAssignModalOpen(false);
      setTemplateId("");
      setDueDate("");
      assignments.reload();
    } catch (err) {
      setFormError(err instanceof Error ? err.message : "No se pudo asignar");
    } finally {
      setSaving(false);
    }
  }

  async function cancel(item: QuestionnaireAssignment) {
    try {
      await questionnaireService.cancelAssignment(item.id);
      assignments.reload();
    } catch (err) {
      globalThis.alert(err instanceof Error ? err.message : "Acción fallida");
    }
  }

  async function viewResponses(item: QuestionnaireAssignment) {
    try {
      const data = await questionnaireService.listResponses(item.id);
      setResponsesTitle(item.templateName ?? "Respuestas");
      setResponses(data);
    } catch (err) {
      globalThis.alert(err instanceof Error ? err.message : "No se pudieron cargar las respuestas");
    }
  }

  return (
    <div>
      <div className="page-header">
        <h2>Cuestionarios</h2>
        <button className="btn btn--primary" onClick={() => setAssignModalOpen(true)}>
          Asignar cuestionario
        </button>
      </div>

      <StateView
        loading={assignments.loading}
        error={assignments.error}
        isEmpty={!assignments.data || assignments.data.length === 0}
        emptyMessage="Sin cuestionarios asignados."
      >
        <table>
          <thead>
            <tr>
              <th>Cuestionario</th>
              <th>Vence</th>
              <th>Estado</th>
              <th />
            </tr>
          </thead>
          <tbody>
            {assignments.data?.map((item) => (
              <tr key={item.id}>
                <td>{item.templateName ?? item.templateId}</td>
                <td>{formatDate(item.dueDate)}</td>
                <td>
                  <StatusBadge status={item.status} />
                </td>
                <td>
                  <div className="btn-row">
                    <button className="btn btn--sm" onClick={() => viewResponses(item)}>
                      Ver respuestas
                    </button>
                    {item.status !== "CANCELLED" && (
                      <button className="btn btn--sm btn--ghost" onClick={() => cancel(item)}>
                        Cancelar
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
        open={assignModalOpen}
        title="Asignar cuestionario"
        onClose={() => setAssignModalOpen(false)}
      >
        <form onSubmit={handleAssign}>
          {formError && <div className="state state--error">{formError}</div>}
          <div className="form-grid">
            <label className="field field--full">
              Cuestionario *
              <select value={templateId} onChange={(e) => setTemplateId(e.target.value)} required>
                <option value="">— Selecciona —</option>
                {templates.data
                  ?.filter((t) => t.active)
                  .map((t) => (
                    <option key={t.id} value={t.id}>
                      {t.name}
                    </option>
                  ))}
              </select>
            </label>
            <label className="field">
              Fecha límite
              <input type="date" value={dueDate} onChange={(e) => setDueDate(e.target.value)} />
            </label>
          </div>
          <div className="modal__actions">
            <button
              type="button"
              className="btn btn--ghost"
              onClick={() => setAssignModalOpen(false)}
            >
              Cancelar
            </button>
            <button type="submit" className="btn btn--primary" disabled={saving}>
              {saving ? "Asignando…" : "Asignar"}
            </button>
          </div>
        </form>
      </Modal>

      <Modal open={responses !== null} title={responsesTitle} onClose={() => setResponses(null)}>
        {responses?.length === 0 && (
          <p className="text-muted">Todavía sin respuestas.</p>
        )}
        {responses?.map((resp) => (
          <div key={resp.id} className="card">
            <p className="text-muted">Enviado: {formatDateTime(resp.submittedAt)}</p>
            <ul>
              {resp.answers.map((ans) => (
                <li key={ans.id}>
                  {ans.answerText ?? ans.answerNumber ?? ans.answerJson ?? "—"}
                </li>
              ))}
            </ul>
          </div>
        ))}
      </Modal>
    </div>
  );
}
