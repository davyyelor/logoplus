import { useState } from "react";
import type { FormEvent } from "react";
import { Modal } from "../../components/Modal";
import { StateView } from "../../components/StateView";
import { StatusBadge } from "../../components/StatusBadge";
import { useAsync } from "../../hooks/useAsync";
import { consentService, consentTemplateService } from "../../services/consentService";
import { guardianService } from "../../services/guardianService";
import type { IssueConsentRequest, PatientConsent } from "../../types/api";
import { formatDate, humanizeEnum } from "../../utils/format";

interface FormState {
  templateId: string;
  guardianId: string;
  expiresOn: string;
}

const EMPTY_FORM: FormState = { templateId: "", guardianId: "", expiresOn: "" };

export function ConsentsTab({ patientId }: Readonly<{ patientId: string }>) {
  const { data, loading, error, reload } = useAsync(
    () => consentService.listForPatient(patientId),
    [patientId],
  );
  const { data: templates } = useAsync(() => consentTemplateService.list(), []);
  const { data: guardians } = useAsync(() => guardianService.listForPatient(patientId), [patientId]);

  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState<FormState>(EMPTY_FORM);
  const [formError, setFormError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const activeTemplates = templates?.filter((t) => t.active) ?? [];

  function openIssue() {
    setForm(EMPTY_FORM);
    setFormError(null);
    setModalOpen(true);
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    if (!form.templateId) {
      setFormError("La plantilla es obligatoria");
      return;
    }
    const request: IssueConsentRequest = {
      templateId: form.templateId,
      guardianId: form.guardianId || null,
      expiresOn: form.expiresOn || null,
    };
    setSaving(true);
    setFormError(null);
    try {
      await consentService.issue(patientId, request);
      setModalOpen(false);
      reload();
    } catch (err) {
      setFormError(err instanceof Error ? err.message : "No se pudo emitir");
    } finally {
      setSaving(false);
    }
  }

  async function sign(consent: PatientConsent) {
    const signature = globalThis.prompt("Texto de la firma (nombre completo de quien firma):");
    if (!signature) {
      return;
    }
    await runAction(() => consentService.sign(consent.id, { signatureText: signature }));
  }

  async function runAction(action: () => Promise<unknown>) {
    try {
      await action();
      reload();
    } catch (err) {
      globalThis.alert(err instanceof Error ? err.message : "Acción fallida");
    }
  }

  function downloadPdf(consent: PatientConsent) {
    consentService
      .downloadPdf(consent.id, `consentimiento-${consent.id}.pdf`)
      .catch((err) => globalThis.alert(err instanceof Error ? err.message : "Descarga fallida"));
  }

  return (
    <div>
      <div className="page-header">
        <h2>Consentimientos</h2>
        <button className="btn btn--primary" onClick={openIssue} disabled={activeTemplates.length === 0}>
          Emitir consentimiento
        </button>
      </div>
      {activeTemplates.length === 0 && (
        <p className="text-muted">Crea una plantilla de consentimiento activa para emitir consentimientos.</p>
      )}

      <StateView
        loading={loading}
        error={error}
        isEmpty={!data || data.length === 0}
        emptyMessage="Sin consentimientos."
      >
        <table>
          <thead>
            <tr>
              <th>Título</th>
              <th>Tipo</th>
              <th>Estado</th>
              <th>Firmado</th>
              <th />
            </tr>
          </thead>
          <tbody>
            {data?.map((consent) => (
              <tr key={consent.id}>
                <td>{consent.title}</td>
                <td>{humanizeEnum(consent.consentType)}</td>
                <td>
                  <StatusBadge status={consent.status} />
                </td>
                <td>{consent.signedAt ? formatDate(consent.signedAt.slice(0, 10)) : "—"}</td>
                <td>
                  <div className="btn-row">
                    <button className="btn btn--sm" onClick={() => downloadPdf(consent)}>
                      PDF
                    </button>
                    {consent.status === "PENDING" && (
                      <button className="btn btn--sm" onClick={() => sign(consent)}>
                        Firmar
                      </button>
                    )}
                    {consent.status === "SIGNED" && (
                      <button className="btn btn--sm" onClick={() => runAction(() => consentService.revoke(consent.id))}>
                        Revocar
                      </button>
                    )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </StateView>

      <Modal open={modalOpen} title="Emitir consentimiento" onClose={() => setModalOpen(false)}>
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
              Tutor (opcional)
              <select
                value={form.guardianId}
                onChange={(e) => setForm((f) => ({ ...f, guardianId: e.target.value }))}
              >
                <option value="">—</option>
                {guardians?.map((g) => (
                  <option key={g.id} value={g.id}>
                    {g.fullName}
                  </option>
                ))}
              </select>
            </label>
            <label className="field">
              Caduca el (opcional)
              <input
                type="date"
                value={form.expiresOn}
                onChange={(e) => setForm((f) => ({ ...f, expiresOn: e.target.value }))}
              />
            </label>
          </div>
          <div className="modal__actions">
            <button type="button" className="btn btn--ghost" onClick={() => setModalOpen(false)}>
              Cancelar
            </button>
            <button type="submit" className="btn btn--primary" disabled={saving}>
              {saving ? "Emitiendo…" : "Emitir"}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
