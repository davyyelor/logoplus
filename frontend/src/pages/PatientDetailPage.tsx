import { useState } from "react";
import { useParams } from "react-router-dom";
import { StateView } from "../components/StateView";
import { StatusBadge } from "../components/StatusBadge";
import { useAsync } from "../hooks/useAsync";
import { patientService } from "../services/patientService";
import { formatDate } from "../utils/format";
import { GuardiansTab } from "./patient-tabs/GuardiansTab";
import { GoalsTab } from "./patient-tabs/GoalsTab";
import { ReportsTab } from "./patient-tabs/ReportsTab";
import { ConsentsTab } from "./patient-tabs/ConsentsTab";
import { DocumentsTab } from "./patient-tabs/DocumentsTab";
import { PatientAgendaTab, PatientSessionsTab } from "./patient-tabs/PatientActivityTabs";

type TabKey =
  | "summary"
  | "guardians"
  | "appointments"
  | "sessions"
  | "goals"
  | "reports"
  | "documents"
  | "consents";

const TABS: { key: TabKey; label: string }[] = [
  { key: "summary", label: "Resumen" },
  { key: "guardians", label: "Tutores" },
  { key: "appointments", label: "Citas" },
  { key: "sessions", label: "Sesiones" },
  { key: "goals", label: "Objetivos" },
  { key: "reports", label: "Informes" },
  { key: "documents", label: "Documentos" },
  { key: "consents", label: "Consentimientos" },
];

export function PatientDetailPage() {
  const { patientId = "" } = useParams();
  const [tab, setTab] = useState<TabKey>("summary");
  const { data: patient, loading, error } = useAsync(() => patientService.get(patientId), [patientId]);

  return (
    <div>
      <StateView loading={loading} error={error}>
        {patient && (
          <>
            <div className="page-header">
              <div>
                <h1>{patient.fullName}</h1>
                <p className="text-muted">
                  {formatDate(patient.birthDate)} · <StatusBadge status={patient.status} />
                </p>
              </div>
            </div>

            <nav className="tabs">
              {TABS.map((t) => (
                <button
                  key={t.key}
                  className={`tabs__item${tab === t.key ? " tabs__item--active" : ""}`}
                  onClick={() => setTab(t.key)}
                >
                  {t.label}
                </button>
              ))}
            </nav>

            <div className="tab-panel">
              {tab === "summary" && (
                <div className="card">
                  <dl className="detail-grid">
                    <dt>Motivo de consulta</dt>
                    <dd>{patient.reasonForConsultation ?? "—"}</dd>
                    <dt>Centro escolar</dt>
                    <dd>{patient.schoolName ?? "—"}</dd>
                    <dt>Derivado por</dt>
                    <dd>{patient.referralSource ?? "—"}</dd>
                    <dt>Terapeuta principal</dt>
                    <dd>{patient.mainTherapistId ?? "—"}</dd>
                    <dt>Notas</dt>
                    <dd>{patient.relevantNotes ?? "—"}</dd>
                  </dl>
                </div>
              )}
              {tab === "guardians" && <GuardiansTab patientId={patientId} />}
              {tab === "appointments" && <PatientAgendaTab patientId={patientId} />}
              {tab === "sessions" && <PatientSessionsTab patientId={patientId} />}
              {tab === "goals" && <GoalsTab patientId={patientId} />}
              {tab === "reports" && <ReportsTab patientId={patientId} />}
              {tab === "documents" && <DocumentsTab patientId={patientId} />}
              {tab === "consents" && <ConsentsTab patientId={patientId} />}
            </div>
          </>
        )}
      </StateView>
    </div>
  );
}
