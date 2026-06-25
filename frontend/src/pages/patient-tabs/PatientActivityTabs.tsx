import { StateView } from "../../components/StateView";
import { StatusBadge } from "../../components/StatusBadge";
import { useAsync } from "../../hooks/useAsync";
import { appointmentService } from "../../services/appointmentService";
import { sessionService } from "../../services/sessionService";
import { formatDate, formatDateTime, humanizeEnum } from "../../utils/format";

export function PatientAgendaTab({ patientId }: Readonly<{ patientId: string }>) {
  const { data, loading, error } = useAsync(
    () => appointmentService.list({ patientId }),
    [patientId],
  );

  return (
    <StateView
      loading={loading}
      error={error}
      isEmpty={!data || data.length === 0}
      emptyMessage="Sin citas para este paciente."
    >
      <table>
        <thead>
          <tr>
            <th>Inicio</th>
            <th>Modalidad</th>
            <th>Estado</th>
          </tr>
        </thead>
        <tbody>
          {data?.map((appt) => (
            <tr key={appt.id}>
              <td>{formatDateTime(appt.startDateTime)}</td>
              <td>{humanizeEnum(appt.locationType)}</td>
              <td>
                <StatusBadge status={appt.status} />
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </StateView>
  );
}

export function PatientSessionsTab({ patientId }: Readonly<{ patientId: string }>) {
  const { data, loading, error } = useAsync(() => sessionService.list({ patientId }), [patientId]);

  return (
    <StateView
      loading={loading}
      error={error}
      isEmpty={!data || data.length === 0}
      emptyMessage="Sin sesiones para este paciente."
    >
      <table>
        <thead>
          <tr>
            <th>Fecha</th>
            <th>Tipo</th>
            <th>Duración</th>
            <th>Resumen</th>
          </tr>
        </thead>
        <tbody>
          {data?.map((session) => (
            <tr key={session.id}>
              <td>{formatDate(session.sessionDate)}</td>
              <td>{humanizeEnum(session.sessionType)}</td>
              <td>{session.durationMinutes != null ? `${session.durationMinutes} min` : "—"}</td>
              <td className="text-muted">{session.summary ?? "—"}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </StateView>
  );
}
