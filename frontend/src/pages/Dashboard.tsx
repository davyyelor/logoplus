import { Link } from "react-router-dom";
import { StatCard } from "../components/StatCard";
import { StateView } from "../components/StateView";
import { useAsync } from "../hooks/useAsync";
import { appointmentService } from "../services/appointmentService";
import { billingService } from "../services/billingService";
import { patientService } from "../services/patientService";
import { sessionService } from "../services/sessionService";
import { useAuth } from "../context/AuthContext";
import { formatCurrency, formatDateTime, todayIso } from "../utils/format";

export function Dashboard() {
  const { user } = useAuth();
  const today = todayIso();

  const patients = useAsync(() => patientService.list({ status: "ACTIVE" }), []);
  const appointments = useAsync(() => appointmentService.list({ from: today, to: today }), [today]);
  const sessions = useAsync(() => sessionService.list({ fromDate: today, toDate: today }), [today]);
  const summary = useAsync(() => billingService.summary(), []);

  const patientName = (id: string) => patients.data?.find((p) => p.id === id)?.fullName ?? id;
  const scheduledToday = appointments.data?.filter((a) => a.status === "SCHEDULED") ?? [];

  return (
    <div>
      <div className="page-header">
        <h1>Hola, {user?.firstName ?? ""}</h1>
      </div>

      <div className="stat-grid">
        <StatCard label="Pacientes activos" value={String(patients.data?.length ?? "—")} />
        <StatCard label="Citas hoy" value={String(scheduledToday.length)} accent="pending" />
        <StatCard label="Sesiones hoy" value={String(sessions.data?.length ?? "—")} />
        <StatCard
          label="Pendiente de cobro"
          value={summary.data ? formatCurrency(summary.data.totalPending, summary.data.currency) : "—"}
          accent="pending"
        />
        <StatCard
          label="Cobrado"
          value={summary.data ? formatCurrency(summary.data.totalPaid, summary.data.currency) : "—"}
          accent="paid"
        />
        <StatCard label="Tarifas activas" value={String(summary.data?.activeFees ?? "—")} />
      </div>

      <div className="card">
        <div className="page-header">
          <h2>Citas de hoy</h2>
          <Link to="/appointments" className="btn btn--sm">
            Ver agenda
          </Link>
        </div>
        <StateView
          loading={appointments.loading}
          error={appointments.error}
          isEmpty={scheduledToday.length === 0}
          emptyMessage="No hay citas programadas para hoy."
        >
          <table>
            <thead>
              <tr>
                <th>Hora</th>
                <th>Paciente</th>
                <th>Fin</th>
              </tr>
            </thead>
            <tbody>
              {scheduledToday.map((appt) => (
                <tr key={appt.id}>
                  <td>{formatDateTime(appt.startDateTime)}</td>
                  <td>{patientName(appt.patientId)}</td>
                  <td>{formatDateTime(appt.endDateTime)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </StateView>
      </div>
    </div>
  );
}
