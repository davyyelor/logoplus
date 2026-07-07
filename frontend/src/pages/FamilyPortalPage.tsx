import { useEffect, useState } from "react";
import { Modal } from "../components/Modal";
import { StateView } from "../components/StateView";
import { StatusBadge } from "../components/StatusBadge";
import { useAsync } from "../hooks/useAsync";
import { familyService } from "../services/familyService";
import { homeworkService } from "../services/homeworkService";
import { evidenceService } from "../services/evidenceService";
import { evolutionService } from "../services/evolutionService";
import { questionnaireService } from "../services/questionnaireService";
import { reminderService } from "../services/reminderService";
import {
  FAMILY_HOMEWORK_STATUSES,
  type AnswerRequest,
  type HomeworkStatus,
  type PatientConsent,
  type QuestionnaireAssignment,
  type QuestionnaireTemplate,
} from "../types/api";
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
  const homework = useAsync(
    () => (patientId ? homeworkService.listForFamily(patientId) : Promise.resolve([])),
    [patientId],
  );
  const evidence = useAsync(
    () => (patientId ? evidenceService.listForPatient(patientId) : Promise.resolve([])),
    [patientId],
  );
  const evolution = useAsync(
    () => (patientId ? evolutionService.familyEvolution(patientId) : Promise.resolve([])),
    [patientId],
  );
  const questionnaires = useAsync(() => questionnaireService.myQuestionnaires(), []);
  const reminders = useAsync(() => reminderService.listForFamily(), []);

  const [noteText, setNoteText] = useState("");
  const [fillTemplate, setFillTemplate] = useState<QuestionnaireTemplate | null>(null);
  const [fillAssignmentId, setFillAssignmentId] = useState<string>("");
  const [answers, setAnswers] = useState<Record<string, string>>({});

  async function changeHomeworkStatus(id: string, status: HomeworkStatus) {
    try {
      await homeworkService.setStatusAsFamily(id, status);
      homework.reload();
    } catch (err) {
      globalThis.alert(err instanceof Error ? err.message : "Acción fallida");
    }
  }

  async function addTextNote() {
    if (!patientId || !noteText.trim()) {
      return;
    }
    try {
      await evidenceService.createTextNote(patientId, {
        type: "TEXT_NOTE",
        textContent: noteText.trim(),
      });
      setNoteText("");
      evidence.reload();
    } catch (err) {
      globalThis.alert(err instanceof Error ? err.message : "No se pudo enviar");
    }
  }

  async function uploadEvidence(file: File | undefined) {
    if (!patientId || !file) {
      return;
    }
    try {
      await evidenceService.upload(patientId, file, { title: file.name });
      evidence.reload();
    } catch (err) {
      globalThis.alert(err instanceof Error ? err.message : "No se pudo subir");
    }
  }

  async function openQuestionnaire(assignment: QuestionnaireAssignment) {
    try {
      const template = await questionnaireService.familyTemplate(assignment.id);
      setFillTemplate(template);
      setFillAssignmentId(assignment.id);
      setAnswers({});
    } catch (err) {
      globalThis.alert(err instanceof Error ? err.message : "No se pudo abrir el cuestionario");
    }
  }

  async function submitQuestionnaire() {
    if (!fillTemplate) {
      return;
    }
    const payload: AnswerRequest[] = fillTemplate.questions.map((q) => {
      const raw = answers[q.id] ?? "";
      if (q.type === "NUMBER" || q.type === "SCALE_1_5") {
        return { questionId: q.id, answerNumber: raw === "" ? null : Number(raw) };
      }
      return { questionId: q.id, answerText: raw || null };
    });
    try {
      await questionnaireService.submitAsFamily(fillAssignmentId, { answers: payload });
      setFillTemplate(null);
      questionnaires.reload();
    } catch (err) {
      globalThis.alert(err instanceof Error ? err.message : "No se pudo enviar");
    }
  }

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
          <h2>Tareas para casa</h2>
          <StateView
            loading={homework.loading}
            error={homework.error}
            isEmpty={homework.data?.length === 0}
            emptyMessage="Sin tareas asignadas."
          >
            <ul className="list">
              {homework.data?.map((item) => (
                <li key={item.id}>
                  <strong>{item.title}</strong>
                  {item.dueDate ? ` · Vence ${formatDate(item.dueDate)}` : ""}{" "}
                  <StatusBadge status={item.status} />
                  {item.instructions ? <p className="text-muted">{item.instructions}</p> : null}
                  <select
                    value={item.status}
                    onChange={(e) => changeHomeworkStatus(item.id, e.target.value as HomeworkStatus)}
                  >
                    <option value={item.status} disabled>
                      Cambiar estado…
                    </option>
                    {FAMILY_HOMEWORK_STATUSES.map((s) => (
                      <option key={s} value={s}>
                        {humanizeEnum(s)}
                      </option>
                    ))}
                  </select>
                </li>
              ))}
            </ul>
          </StateView>
        </section>

        <section className="card">
          <h2>Evolución</h2>
          <StateView
            loading={evolution.loading}
            error={evolution.error}
            isEmpty={evolution.data?.length === 0}
            emptyMessage="Sin datos de evolución disponibles."
          >
            {evolution.data?.map((item) => (
              <div key={item.metric.id}>
                <h3>
                  {item.metric.name}
                  {item.metric.unit ? ` (${item.metric.unit})` : ""}
                </h3>
                <ul className="list">
                  {item.entries.map((entry) => (
                    <li key={entry.id}>
                      {formatDate(entry.entryDate)}: {entry.value}
                    </li>
                  ))}
                </ul>
              </div>
            ))}
          </StateView>
        </section>

        <section className="card">
          <h2>Evidencias</h2>
          <StateView
            loading={evidence.loading}
            error={evidence.error}
            isEmpty={evidence.data?.length === 0}
            emptyMessage="Todavía no has aportado evidencias."
          >
            <ul className="list">
              {evidence.data?.map((item) => (
                <li key={item.id}>
                  {humanizeEnum(item.type)} · {item.title ?? item.textContent ?? "Adjunto"}{" "}
                  <StatusBadge status={item.reviewStatus} />
                </li>
              ))}
            </ul>
          </StateView>
          <div className="form-grid">
            <label className="field field--full">
              Añadir nota
              <textarea
                value={noteText}
                onChange={(e) => setNoteText(e.target.value)}
                rows={2}
                placeholder="Escribe cómo ha ido la tarea en casa"
              />
            </label>
            <div className="btn-row">
              <button className="btn btn--sm btn--primary" onClick={addTextNote}>
                Enviar nota
              </button>
              <label className="btn btn--sm">
                Subir archivo
                <input
                  type="file"
                  style={{ display: "none" }}
                  onChange={(e) => uploadEvidence(e.target.files?.[0])}
                />
              </label>
            </div>
          </div>
        </section>

        <section className="card">
          <h2>Cuestionarios</h2>
          <StateView
            loading={questionnaires.loading}
            error={questionnaires.error}
            isEmpty={questionnaires.data?.length === 0}
            emptyMessage="Sin cuestionarios pendientes."
          >
            <ul className="list">
              {questionnaires.data?.map((item) => (
                <li key={item.id}>
                  {item.templateName ?? item.templateId} <StatusBadge status={item.status} />{" "}
                  {item.status === "PENDING" && (
                    <button className="btn btn--sm" onClick={() => openQuestionnaire(item)}>
                      Responder
                    </button>
                  )}
                </li>
              ))}
            </ul>
          </StateView>
        </section>

        <section className="card">
          <h2>Recordatorios</h2>
          <StateView
            loading={reminders.loading}
            error={reminders.error}
            isEmpty={reminders.data?.length === 0}
            emptyMessage="No tienes recordatorios."
          >
            <ul className="list">
              {reminders.data?.map((item) => (
                <li key={item.id}>
                  {formatDateTime(item.remindAt)} · {item.title}{" "}
                  <StatusBadge status={item.status} />
                  {item.message ? ` — ${item.message}` : ""}
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

      <Modal
        open={fillTemplate !== null}
        title={fillTemplate?.name ?? "Cuestionario"}
        onClose={() => setFillTemplate(null)}
      >
        <div className="form-grid">
          {fillTemplate?.questions.map((q) => (
            <label key={q.id} className="field field--full">
              {q.text}
              {q.type === "NUMBER" || q.type === "SCALE_1_5" ? (
                <input
                  type="number"
                  step="any"
                  value={answers[q.id] ?? ""}
                  onChange={(e) => setAnswers((a) => ({ ...a, [q.id]: e.target.value }))}
                />
              ) : (
                <textarea
                  rows={2}
                  value={answers[q.id] ?? ""}
                  onChange={(e) => setAnswers((a) => ({ ...a, [q.id]: e.target.value }))}
                />
              )}
            </label>
          ))}
        </div>
        <div className="modal__actions">
          <button type="button" className="btn btn--ghost" onClick={() => setFillTemplate(null)}>
            Cancelar
          </button>
          <button type="button" className="btn btn--primary" onClick={submitQuestionnaire}>
            Enviar respuestas
          </button>
        </div>
      </Modal>
    </div>
  );
}
