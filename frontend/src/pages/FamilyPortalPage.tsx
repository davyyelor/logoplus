import { useEffect, useState } from "react";
import { StateView } from "../components/StateView";
import { StatusBadge } from "../components/StatusBadge";
import { useAsync } from "../hooks/useAsync";
import { familyService } from "../services/familyService";
import type { PatientConsent } from "../types/api";
import { useAuth } from "../context/AuthContext";
import { formatDate, formatDateTime, humanizeEnum } from "../utils/format";

export function FamilyPortalPage() {
  const { user } = useAuth();
  const patients = useAsync(() => familyService.myPatients(), []);
  const [patientId, setPatientId] = useState<string>("");

  useEffect(() => {
    if (!patientId && patients.data && patients.data.length > 0) {
      setPatientId(patients.data[0].id);
    }
  }, [patients.data, patientId]);

  const appointments = useAsync(
    () => (patientId ? familyService.appointments(patientId) : Promise.resolve([])),
    [patientId],
  );
  const sessions = useAsync(
    () => (patientId ? familyService.sessions(patientId) : Promise.resolve([])),
    [patientId],
  );
  const documents = useAsync(
    () => (patientId ? familyService.documents(patientId) : Promise.resolve([])),
    [patientId],
  );
  const reports = useAsync(
    () => (patientId ? familyService.reports(patientId) : Promise.resolve([])),
    [patientId],
  );
  const consents = useAsync(
    () => (patientId ? familyService.consents(patientId) : Promise.resolve([])),
    [patientId],
  );

  async function signConsent(consent: PatientConsent) {
    const signature = globalThis.prompt("Escribe tu nombre completo para firmar:");
    if (!signature) {
      return;
    }
    try {
      await familyService.signConsent(consent.id, { signatureText: signature });
      consents.reload();
    } catch (err) {
      globalThis.alert(err instanceof Error ? err.message : "No se pudo firmar");
    }
  }

  function onError(promise: Promise<unknown>) {
    promise.catch((err) => globalThis.alert(err instanceof Error ? err.message : "Descarga fallida"));
  }

  return (
    <div className="portal">
      <div className="page-header">
        <h1>Portal familiar</h1>
        <p className="text-muted">Hola, {user?.firstName ?? ""}</p>
      </div>

      <StateView
        loading={patients.loading}
        error={patients.error}
        isEmpty={!patients.data || patients.data.length === 0}
        emptyMessage="No tienes pacientes vinculados todavía."
      >
        {patients.data && patients.data.length > 1 && (
          <label className="field">
            Paciente
            <select value={patientId} onChange={(e) => setPatientId(e.target.value)}>
              {patients.data.map((p) => (
                <option key={p.id} value={p.id}>
                  {p.fullName}
                </option>
              ))}
            </select>
          </label>
        )}

        <section className="card">
          <h2>Próximas citas</h2>
          <StateView
            loading={appointments.loading}
            error={appointments.error}
            isEmpty={appointments.data?.length === 0}
            emptyMessage="Sin citas."
          >
            <ul className="list">
              {appointments.data?.map((appt) => (
                <li key={appt.id}>
                  {formatDateTime(appt.startDateTime)} — {humanizeEnum(appt.locationType)}{" "}
                  <StatusBadge status={appt.status} />
                </li>
              ))}
            </ul>
          </StateView>
        </section>

        <section className="card">
          <h2>Sesiones</h2>
          <StateView
            loading={sessions.loading}
            error={sessions.error}
            isEmpty={sessions.data?.length === 0}
            emptyMessage="Sin sesiones."
          >
            <ul className="list">
              {sessions.data?.map((session) => (
                <li key={session.id}>
                  {formatDate(session.sessionDate)} — {humanizeEnum(session.sessionType)}
                  {session.homework ? ` · Tareas: ${session.homework}` : ""}
                </li>
              ))}
            </ul>
          </StateView>
        </section>

        <section className="card">
          <h2>Informes compartidos</h2>
          <StateView
            loading={reports.loading}
            error={reports.error}
            isEmpty={reports.data?.length === 0}
            emptyMessage="Sin informes compartidos."
          >
            <ul className="list">
              {reports.data?.map((report) => (
                <li key={report.id}>
                  {report.title}{" "}
                  <button
                    className="btn btn--sm"
                    onClick={() => onError(familyService.downloadReportPdf(report.id, `${report.title}.pdf`))}
                  >
                    Descargar PDF
                  </button>
                </li>
              ))}
            </ul>
          </StateView>
        </section>

        <section className="card">
          <h2>Documentos</h2>
          <StateView
            loading={documents.loading}
            error={documents.error}
            isEmpty={documents.data?.length === 0}
            emptyMessage="Sin documentos."
          >
            <ul className="list">
              {documents.data?.map((doc) => (
                <li key={doc.id}>
                  {doc.originalFileName}{" "}
                  <button
                    className="btn btn--sm"
                    onClick={() => onError(familyService.downloadDocument(doc.id, doc.originalFileName))}
                  >
                    Descargar
                  </button>
                </li>
              ))}
            </ul>
          </StateView>
        </section>

        <section className="card">
          <h2>Consentimientos</h2>
          <StateView
            loading={consents.loading}
            error={consents.error}
            isEmpty={consents.data?.length === 0}
            emptyMessage="Sin consentimientos."
          >
            <ul className="list">
              {consents.data?.map((consent) => (
                <li key={consent.id}>
                  {consent.title} <StatusBadge status={consent.status} />{" "}
                  {consent.status === "PENDING" && (
                    <button className="btn btn--sm" onClick={() => signConsent(consent)}>
                      Firmar
                    </button>
                  )}
                  <button
                    className="btn btn--sm"
                    onClick={() => onError(familyService.downloadConsentPdf(consent.id, `consentimiento-${consent.id}.pdf`))}
                  >
                    PDF
                  </button>
                </li>
              ))}
            </ul>
          </StateView>
        </section>
      </StateView>
    </div>
  );
}
