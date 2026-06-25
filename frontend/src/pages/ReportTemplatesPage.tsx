import { useState } from "react";
import type { FormEvent } from "react";
import { Modal } from "../components/Modal";
import { StateView } from "../components/StateView";
import { useAsync } from "../hooks/useAsync";
import { reportTemplateService } from "../services/reportService";
import { REPORT_TYPES, type ReportTemplate, type ReportType } from "../types/api";
import { humanizeEnum } from "../utils/format";

interface FormState {
  name: string;
  reportType: ReportType;
  contentTemplate: string;
}

const EMPTY_FORM: FormState = {
  name: "",
  reportType: "EVOLUTION",
  contentTemplate:
    "INFORME\n\nCentro: {{clinic.name}}\nFecha: {{today}}\n\nPaciente: {{patient.fullName}}\n\nRecomendaciones:\n{{recommendations}}\n",
};

export function ReportTemplatesPage() {
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<ReportTemplate | null>(null);
  const [form, setForm] = useState<FormState>(EMPTY_FORM);
  const [formError, setFormError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const { data, loading, error, reload } = useAsync(() => reportTemplateService.list(), []);

  function openCreate() {
    setEditing(null);
    setForm(EMPTY_FORM);
    setFormError(null);
    setModalOpen(true);
  }

  function openEdit(template: ReportTemplate) {
    setEditing(template);
    setForm({
      name: template.name,
      reportType: template.reportType,
      contentTemplate: template.contentTemplate,
    });
    setFormError(null);
    setModalOpen(true);
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    if (!form.name.trim()) {
      setFormError("El nombre es obligatorio");
      return;
    }
    setSaving(true);
    setFormError(null);
    try {
      const request = {
        name: form.name.trim(),
        reportType: form.reportType,
        contentTemplate: form.contentTemplate,
      };
      if (editing) {
        await reportTemplateService.update(editing.id, request);
      } else {
        await reportTemplateService.create(request);
      }
      setModalOpen(false);
      reload();
    } catch (err) {
      setFormError(err instanceof Error ? err.message : "No se pudo guardar");
    } finally {
      setSaving(false);
    }
  }

  async function toggleActive(template: ReportTemplate) {
    try {
      if (template.active) {
        await reportTemplateService.deactivate(template.id);
      } else {
        await reportTemplateService.activate(template.id);
      }
      reload();
    } catch (err) {
      globalThis.alert(err instanceof Error ? err.message : "Acción fallida");
    }
  }

  return (
    <div>
      <div className="page-header">
        <h1>Plantillas de informe</h1>
        <button className="btn btn--primary" onClick={openCreate}>
          Nueva plantilla
        </button>
      </div>

      <p className="text-muted">
        Variables disponibles: {"{{patient.fullName}}, {{patient.birthDate}}, {{patient.age}}, "}
        {"{{clinic.name}}, {{today}}, {{recommendations}}"}
      </p>

      <StateView
        loading={loading}
        error={error}
        isEmpty={!data || data.length === 0}
        emptyMessage="No hay plantillas."
      >
        <table>
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Tipo</th>
              <th>Activa</th>
              <th />
            </tr>
          </thead>
          <tbody>
            {data?.map((template) => (
              <tr key={template.id}>
                <td>{template.name}</td>
                <td>{humanizeEnum(template.reportType)}</td>
                <td>{template.active ? "Sí" : "No"}</td>
                <td>
                  <div className="btn-row">
                    <button className="btn btn--sm" onClick={() => openEdit(template)}>
                      Editar
                    </button>
                    <button className="btn btn--sm" onClick={() => toggleActive(template)}>
                      {template.active ? "Desactivar" : "Activar"}
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
        title={editing ? "Editar plantilla" : "Nueva plantilla"}
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
              Tipo
              <select
                value={form.reportType}
                onChange={(e) => setForm((f) => ({ ...f, reportType: e.target.value as ReportType }))}
              >
                {REPORT_TYPES.map((t) => (
                  <option key={t} value={t}>
                    {humanizeEnum(t)}
                  </option>
                ))}
              </select>
            </label>
            <label className="field field--full">
              Contenido (plantilla)
              <textarea
                value={form.contentTemplate}
                onChange={(e) => setForm((f) => ({ ...f, contentTemplate: e.target.value }))}
                rows={10}
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
