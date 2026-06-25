import { useState } from "react";
import type { FormEvent } from "react";
import { Modal } from "../components/Modal";
import { StateView } from "../components/StateView";
import { useAsync } from "../hooks/useAsync";
import { consentTemplateService } from "../services/consentService";
import { CONSENT_TYPES, type ConsentTemplate, type ConsentType } from "../types/api";
import { humanizeEnum } from "../utils/format";

interface FormState {
  name: string;
  consentType: ConsentType;
  body: string;
}

const EMPTY_FORM: FormState = {
  name: "",
  consentType: "DATA_PROCESSING",
  body: "",
};

export function ConsentTemplatesPage() {
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<ConsentTemplate | null>(null);
  const [form, setForm] = useState<FormState>(EMPTY_FORM);
  const [formError, setFormError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const { data, loading, error, reload } = useAsync(() => consentTemplateService.list(), []);

  function openCreate() {
    setEditing(null);
    setForm(EMPTY_FORM);
    setFormError(null);
    setModalOpen(true);
  }

  function openEdit(template: ConsentTemplate) {
    setEditing(template);
    setForm({ name: template.name, consentType: template.consentType, body: template.body });
    setFormError(null);
    setModalOpen(true);
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    if (!form.name.trim() || !form.body.trim()) {
      setFormError("Nombre y texto son obligatorios");
      return;
    }
    setSaving(true);
    setFormError(null);
    try {
      const request = { name: form.name.trim(), consentType: form.consentType, body: form.body };
      if (editing) {
        await consentTemplateService.update(editing.id, request);
      } else {
        await consentTemplateService.create(request);
      }
      setModalOpen(false);
      reload();
    } catch (err) {
      setFormError(err instanceof Error ? err.message : "No se pudo guardar");
    } finally {
      setSaving(false);
    }
  }

  async function toggleActive(template: ConsentTemplate) {
    try {
      if (template.active) {
        await consentTemplateService.deactivate(template.id);
      } else {
        await consentTemplateService.activate(template.id);
      }
      reload();
    } catch (err) {
      globalThis.alert(err instanceof Error ? err.message : "Acción fallida");
    }
  }

  return (
    <div>
      <div className="page-header">
        <h1>Plantillas de consentimiento</h1>
        <button className="btn btn--primary" onClick={openCreate}>
          Nueva plantilla
        </button>
      </div>

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
                <td>{humanizeEnum(template.consentType)}</td>
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
                value={form.consentType}
                onChange={(e) => setForm((f) => ({ ...f, consentType: e.target.value as ConsentType }))}
              >
                {CONSENT_TYPES.map((t) => (
                  <option key={t} value={t}>
                    {humanizeEnum(t)}
                  </option>
                ))}
              </select>
            </label>
            <label className="field field--full">
              Texto del consentimiento *
              <textarea
                value={form.body}
                onChange={(e) => setForm((f) => ({ ...f, body: e.target.value }))}
                rows={8}
                required
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
