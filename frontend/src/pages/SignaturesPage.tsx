import { useEffect, useMemo, useState } from "react";
import { StateView } from "../components/StateView";
import { StatusBadge } from "../components/StatusBadge";
import { useAsync } from "../hooks/useAsync";
import { familyService } from "../services/familyService";
import { familySignatureService } from "../services/signatureService";
import { useAuth } from "../context/AuthContext";
import { formatDateTime } from "../utils/format";
import type { SignatureRequest } from "../types/api";

const DOCUMENT_TYPES = ["CONSENT", "REPORT", "OTHER"];

export function SignaturesPage() {
  const { user } = useAuth();
  const patients = useAsync(() => familyService.myPatients(), []);
  const status = useAsync(() => familySignatureService.status(), []);
  const [patientId, setPatientId] = useState<string>("");

  useEffect(() => {
    if (!patientId && patients.data && patients.data.length > 0) {
      setPatientId(patients.data[0].id);
    }
  }, [patients.data, patientId]);

  const signatures = useAsync(
    () => (patientId ? familySignatureService.listForPatient(patientId) : Promise.resolve([])),
    [patientId],
  );

  const defaultSignerName = useMemo(
    () => [user?.firstName, user?.lastName].filter(Boolean).join(" ").trim(),
    [user],
  );

  const [documentType, setDocumentType] = useState<string>("CONSENT");
  const [documentId, setDocumentId] = useState<string>("");
  const [signerName, setSignerName] = useState<string>("");
  const [note, setNote] = useState<string>("");
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    setSignerName(defaultSignerName);
  }, [defaultSignerName]);

  async function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    if (!patientId) {
      return;
    }
    const request: SignatureRequest = {
      documentType,
      documentId: documentId.trim() || null,
      signerName: signerName.trim(),
      note: note.trim() || null,
    };
    setSubmitting(true);
    try {
      await familySignatureService.sign(patientId, request);
      setDocumentId("");
      setNote("");
      signatures.reload();
    } catch (err) {
      globalThis.alert(err instanceof Error ? err.message : "No se pudo firmar el documento");
    } finally {
      setSubmitting(false);
    }
  }

  const disabled = status.data ? !status.data.enabled : false;

  return (
    <div>
      <div className="page-header">
        <h1>Firmas electrónicas</h1>
      </div>

      <StateView loading={patients.loading} error={patients.error}>
        <div className="form-grid">
          <label>
            Paciente
            <select value={patientId} onChange={(event) => setPatientId(event.target.value)}>
              {patients.data?.map((patient) => (
                <option key={patient.id} value={patient.id}>
                  {patient.firstName} {patient.lastName}
                </option>
              ))}
            </select>
          </label>
        </div>

        {status.data && (
          <p className="hint">
            Proveedor de firma: {status.data.provider} · {status.data.enabled ? "activo" : "no disponible"}
          </p>
        )}

        <form onSubmit={handleSubmit} className="card">
          <h2>Firmar un documento</h2>
          <div className="form-grid">
            <label>
              Tipo de documento
              <select value={documentType} onChange={(event) => setDocumentType(event.target.value)}>
                {DOCUMENT_TYPES.map((type) => (
                  <option key={type} value={type}>
                    {type}
                  </option>
                ))}
              </select>
            </label>
            <label>
              Referencia (opcional)
              <input value={documentId} onChange={(event) => setDocumentId(event.target.value)} />
            </label>
            <label>
              Firmado por
              <input
                value={signerName}
                onChange={(event) => setSignerName(event.target.value)}
                required
              />
            </label>
            <label>
              Nota (opcional)
              <input value={note} onChange={(event) => setNote(event.target.value)} />
            </label>
          </div>
          <button
            className="btn btn--primary"
            type="submit"
            disabled={submitting || disabled || !patientId || !signerName.trim()}
          >
            Firmar
          </button>
          {disabled && <p className="hint">La firma no está disponible en este momento.</p>}
        </form>

        <StateView
          loading={signatures.loading}
          error={signatures.error}
          isEmpty={!signatures.data || signatures.data.length === 0}
          emptyMessage="Aún no hay firmas para este paciente."
        >
          <table>
            <thead>
              <tr>
                <th>Documento</th>
                <th>Firmado por</th>
                <th>Fecha</th>
                <th>Estado</th>
                <th>Huella</th>
              </tr>
            </thead>
            <tbody>
              {signatures.data?.map((signature) => (
                <tr key={signature.id}>
                  <td>
                    {signature.documentType}
                    {signature.documentId ? ` · ${signature.documentId}` : ""}
                  </td>
                  <td>{signature.signerName}</td>
                  <td>{signature.signedAt ? formatDateTime(signature.signedAt) : "—"}</td>
                  <td>
                    <StatusBadge status={signature.status} />
                  </td>
                  <td>
                    <code>{signature.signatureHash ? signature.signatureHash.slice(0, 12) : "—"}</code>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </StateView>
      </StateView>
    </div>
  );
}
