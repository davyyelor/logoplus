import { useRef, useState } from "react";
import type { FormEvent } from "react";
import { Modal } from "../../components/Modal";
import { StateView } from "../../components/StateView";
import { useAsync } from "../../hooks/useAsync";
import { documentService } from "../../services/documentService";
import { DOCUMENT_TYPES, type DocumentMeta, type DocumentType } from "../../types/api";
import { formatDate, humanizeEnum } from "../../utils/format";

function formatBytes(bytes: number): string {
  if (bytes < 1024) {
    return `${bytes} B`;
  }
  if (bytes < 1024 * 1024) {
    return `${(bytes / 1024).toFixed(1)} KB`;
  }
  return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
}

export function DocumentsTab({ patientId }: Readonly<{ patientId: string }>) {
  const { data, loading, error, reload } = useAsync(
    () => documentService.listForPatient(patientId),
    [patientId],
  );
  const [modalOpen, setModalOpen] = useState(false);
  const [documentType, setDocumentType] = useState<DocumentType>("OTHER");
  const [visibleToFamily, setVisibleToFamily] = useState(false);
  const [file, setFile] = useState<File | null>(null);
  const [formError, setFormError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);

  function openUpload() {
    setDocumentType("OTHER");
    setVisibleToFamily(false);
    setFile(null);
    setFormError(null);
    setModalOpen(true);
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    if (!file) {
      setFormError("Selecciona un archivo");
      return;
    }
    setSaving(true);
    setFormError(null);
    try {
      await documentService.upload(patientId, file, documentType, visibleToFamily);
      setModalOpen(false);
      reload();
    } catch (err) {
      setFormError(err instanceof Error ? err.message : "No se pudo subir");
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

  function download(doc: DocumentMeta) {
    documentService
      .download(doc.id, doc.originalFileName)
      .catch((err) => globalThis.alert(err instanceof Error ? err.message : "Descarga fallida"));
  }

  return (
    <div>
      <div className="page-header">
        <h2>Documentos</h2>
        <button className="btn btn--primary" onClick={openUpload}>
          Subir documento
        </button>
      </div>

      <StateView
        loading={loading}
        error={error}
        isEmpty={!data || data.length === 0}
        emptyMessage="Sin documentos."
      >
        <table>
          <thead>
            <tr>
              <th>Archivo</th>
              <th>Tipo</th>
              <th>Tamaño</th>
              <th>Familia</th>
              <th>Subido</th>
              <th />
            </tr>
          </thead>
          <tbody>
            {data?.map((doc) => (
              <tr key={doc.id}>
                <td>{doc.originalFileName}</td>
                <td>{humanizeEnum(doc.documentType)}</td>
                <td>{formatBytes(doc.sizeBytes)}</td>
                <td>{doc.visibleToFamily ? "Visible" : "Oculto"}</td>
                <td>{formatDate(doc.createdAt.slice(0, 10))}</td>
                <td>
                  <div className="btn-row">
                    <button className="btn btn--sm" onClick={() => download(doc)}>
                      Descargar
                    </button>
                    {doc.visibleToFamily ? (
                      <button className="btn btn--sm" onClick={() => runAction(() => documentService.hideFromFamily(doc.id))}>
                        Ocultar a familia
                      </button>
                    ) : (
                      <button className="btn btn--sm" onClick={() => runAction(() => documentService.shareWithFamily(doc.id))}>
                        Compartir con familia
                      </button>
                    )}
                    <button className="btn btn--sm" onClick={() => runAction(() => documentService.remove(doc.id))}>
                      Eliminar
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </StateView>

      <Modal open={modalOpen} title="Subir documento" onClose={() => setModalOpen(false)}>
        <form onSubmit={handleSubmit}>
          {formError && <div className="state state--error">{formError}</div>}
          <div className="form-grid">
            <label className="field field--full">
              Archivo *
              <input
                ref={inputRef}
                type="file"
                onChange={(e) => setFile(e.target.files?.[0] ?? null)}
                required
              />
            </label>
            <label className="field">
              Tipo
              <select
                value={documentType}
                onChange={(e) => setDocumentType(e.target.value as DocumentType)}
              >
                {DOCUMENT_TYPES.map((t) => (
                  <option key={t} value={t}>
                    {humanizeEnum(t)}
                  </option>
                ))}
              </select>
            </label>
            <label className="field field--check">
              <input
                type="checkbox"
                checked={visibleToFamily}
                onChange={(e) => setVisibleToFamily(e.target.checked)}
              />
              Visible para la familia
            </label>
          </div>
          <div className="modal__actions">
            <button type="button" className="btn btn--ghost" onClick={() => setModalOpen(false)}>
              Cancelar
            </button>
            <button type="submit" className="btn btn--primary" disabled={saving}>
              {saving ? "Subiendo…" : "Subir"}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
