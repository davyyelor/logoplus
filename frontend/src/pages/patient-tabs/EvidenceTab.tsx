import { StateView } from "../../components/StateView";
import { StatusBadge } from "../../components/StatusBadge";
import { useAsync } from "../../hooks/useAsync";
import { evidenceService } from "../../services/evidenceService";
import type { EvidenceReviewStatus, FamilyEvidence } from "../../types/api";
import { formatDateTime, humanizeEnum } from "../../utils/format";

export function EvidenceTab({ patientId }: Readonly<{ patientId: string }>) {
  const { data, loading, error, reload } = useAsync(
    () => evidenceService.listForPatient(patientId),
    [patientId],
  );

  async function review(item: FamilyEvidence, status: EvidenceReviewStatus) {
    try {
      await evidenceService.review(item.id, status);
      reload();
    } catch (err) {
      globalThis.alert(err instanceof Error ? err.message : "Acción fallida");
    }
  }

  return (
    <div>
      <div className="page-header">
        <h2>Evidencias de la familia</h2>
      </div>

      <StateView
        loading={loading}
        error={error}
        isEmpty={!data || data.length === 0}
        emptyMessage="La familia todavía no ha aportado evidencias."
      >
        <table>
          <thead>
            <tr>
              <th>Fecha</th>
              <th>Tipo</th>
              <th>Título</th>
              <th>Contenido</th>
              <th>Estado</th>
              <th />
            </tr>
          </thead>
          <tbody>
            {data?.map((item) => (
              <tr key={item.id}>
                <td>{formatDateTime(item.createdAt)}</td>
                <td>{humanizeEnum(item.type)}</td>
                <td>{item.title ?? "—"}</td>
                <td className="text-muted">
                  {item.type === "TEXT_NOTE"
                    ? (item.textContent ?? "—")
                    : (item.description ?? "Archivo adjunto")}
                </td>
                <td>
                  <StatusBadge status={item.reviewStatus} />
                </td>
                <td>
                  <div className="btn-row">
                    {item.reviewStatus !== "REVIEWED" && (
                      <button className="btn btn--sm" onClick={() => review(item, "REVIEWED")}>
                        Marcar revisada
                      </button>
                    )}
                    {item.reviewStatus !== "REJECTED" && (
                      <button
                        className="btn btn--sm btn--ghost"
                        onClick={() => review(item, "REJECTED")}
                      >
                        Rechazar
                      </button>
                    )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </StateView>
    </div>
  );
}
