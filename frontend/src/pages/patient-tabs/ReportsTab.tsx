import { useState } from "react";
import type { FormEvent } from "react";
import { Modal } from "../../components/Modal";
import { StateView } from "../../components/StateView";
import { StatusBadge } from "../../components/StatusBadge";
import { useAsync } from "../../hooks/useAsync";
import { goalService } from "../../services/goalService";
import { reportService, reportTemplateService } from "../../services/reportService";
import type { GeneratedReport, GenerateReportRequest } from "../../types/api";
import { formatDate, humanizeEnum } from "../../utils/format";

interface FormState {
  templateId: string;
  title: string;
  includeSessions: boolean;
  sessionsFrom: string;
  sessionsTo: string;
  includeGoals: boolean;
  recommendations: string;
}

const EMPTY_FORM: FormState = {
  templateId: "",
  title: "",
  includeSessions: true,
  sessionsFrom: "",
  sessionsTo: "",
  includeGoals: true,
  recommendations: "",
};

export function ReportsTab({ patientId }: Readonly<{ patientId: string }>) {
  const { data, loading, error, reload } = useAsync(
    () => reportService.listForPatient(patientId),
    [patientId],
  );
  const { data: templates } = useAsync(() => reportTemplateService.list(), []);
  const { data: goals } = useAsync(() => goalService.listForPatient(patientId), [patientId]);

  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState<FormState>(EMPTY_FORM);
  const [selectedGoals, setSelectedGoals] = useState<string[]>([]);
  const [formError, setFormError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const activeTemplates = templates?.filter((t) => t.active) ?? [];

  function openGenerate() {
    setForm(EMPTY_FORM);
    setSelectedGoals([]);
    setFormError(null);
    setModalOpen(true);
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    if (!form.templateId || !form.title.trim()) {
      setFormError("Plantilla y título son obligatorios");
      return;
    }
    const request: GenerateReportRequest = {
      templateId: form.templateId,
      title: form.title.trim(),
      includeSessions: form.includeSessions,
      sessionsFrom: form.sessionsFrom || null,
      sessionsTo: form.sessionsTo || null,
      includeGoals: form.includeGoals,
      goalIds: form.includeGoals ? selectedGoals : [],
      recommendations: form.recommendations.trim() || null,
    };
    setSaving(true);
    setFormError(null);
    try {
      await reportService.generate(patientId, request);
      setModalOpen(false);
      reload();
    } catch (err) {
      setFormError(err instanceof Error ? err.message : "No se pudo generar");
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

  function downloadPdf(report: GeneratedReport) {
    reportService
      .downloadPdf(report.id, `${report.title}.pdf`)
      .catch((err) => globalThis.alert(err instanceof Error ? err.message : "Descarga fallida"));
  }

  return (
    <div>
      <div className="page-header">
        <h2>Informes</h2>
        <button className="btn btn--primary" onClick={openGenerate} disabled={activeTemplates.length === 0}>
          Generar informe
        </button>
      </div>
      {activeTemplates.length === 0 && (
        <p className="text-muted">Crea una plantilla de informe activa para poder generar informes.</p>
      )}

      <StateView
        loading={loading}
        error={error}
        isEmpty={!data || data.length === 0}
        emptyMessage="Sin informes generados."
      >
        <table>
          <thead>
            <tr>
              <th>Título</th>
              <th>Tipo</th>
              <th>Estado</th>
              <th>Fecha</th>
              <th />
            </tr>
          </thead>
          <tbody>
            {data?.map((report) => (
              <tr key={report.id}>
                <td>{report.title}</td>
                <td>{humanizeEnum(report.reportType)}</td>
                <td>
                  <StatusBadge status={report.status} />
                </td>
                <td>{formatDate(report.createdAt.slice(0, 10))}</td>
                <td>
                  <div className="btn-row">
                    <button className="btn btn--sm" onClick={() => downloadPdf(report)}>
                      PDF
                    </button>
                    {report.status !== "SHARED_WITH_FAMILY" && report.status !== "ARCHIVED" && (
                      <button
                        className="btn btn--sm"
                        onClick={() => runAction(() => reportService.shareWithFamily(report.id))}
                      >
                        Compartir con familia
                      </button>
                    )}
                    {report.status !== "ARCHIVED" && (
                      <button
                        className="btn btn--sm"
                        onClick={() => runAction(() => reportService.archive(report.id))}
                      >
                        Archivar
                      </button>
                    )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </StateView>

      <Modal open={modalOpen} title="Generar informe" onClose={() => setModalOpen(false)}>
        <form onSubmit={handleSubmit}>
          {formError && <div className="state state--error">{formError}</div>}
          <div className="form-grid">
            <label className="field">
              Plantilla *
              <select
                value={form.templateId}
                onChange={(e) => setForm((f) => ({ ...f, templateId: e.target.value }))}
                required
              >
                <option value="">Seleccionar…</option>
                {activeTemplates.map((t) => (
                  <option key={t.id} value={t.id}>
                    {t.name}
                  </option>
                ))}
              </select>
            </label>
            <label className="field">
              Título *
              <input
                value={form.title}
                onChange={(e) => setForm((f) => ({ ...f, title: e.target.value }))}
                required
              />
            </label>
            <label className="field field--check">
              <input
                type="checkbox"
                checked={form.includeSessions}
                onChange={(e) => setForm((f) => ({ ...f, includeSessions: e.target.checked }))}
              />
              Incluir resumen de sesiones
            </label>
            <label className="field field--check">
              <input
                type="checkbox"
                checked={form.includeGoals}
                onChange={(e) => setForm((f) => ({ ...f, includeGoals: e.target.checked }))}
              />
              Incluir objetivos
            </label>
            {form.includeSessions && (
              <>
                <label className="field">
                  Sesiones desde
                  <input
                    type="date"
                    value={form.sessionsFrom}
                    onChange={(e) => setForm((f) => ({ ...f, sessionsFrom: e.target.value }))}
                  />
                </label>
                <label className="field">
                  Sesiones hasta
                  <input
                    type="date"
                    value={form.sessionsTo}
                    onChange={(e) => setForm((f) => ({ ...f, sessionsTo: e.target.value }))}
                  />
                </label>
              </>
            )}
            {form.includeGoals && goals && goals.length > 0 && (
              <fieldset className="field field--full">
                <legend>Objetivos a incluir</legend>
                {goals.map((goal) => (
                  <label key={goal.id} className="field--check">
                    <input
                      type="checkbox"
                      checked={selectedGoals.includes(goal.id)}
                      onChange={(e) =>
                        setSelectedGoals((prev) =>
                          e.target.checked ? [...prev, goal.id] : prev.filter((id) => id !== goal.id),
                        )
                      }
                    />
                    {goal.title}
                  </label>
                ))}
              </fieldset>
            )}
            <label className="field field--full">
              Recomendaciones
              <textarea
                value={form.recommendations}
                onChange={(e) => setForm((f) => ({ ...f, recommendations: e.target.value }))}
                rows={3}
              />
            </label>
          </div>
          <div className="modal__actions">
            <button type="button" className="btn btn--ghost" onClick={() => setModalOpen(false)}>
              Cancelar
            </button>
            <button type="submit" className="btn btn--primary" disabled={saving}>
              {saving ? "Generando…" : "Generar"}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
