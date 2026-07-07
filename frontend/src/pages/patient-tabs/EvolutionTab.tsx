import { useState } from "react";
import type { FormEvent } from "react";
import { Modal } from "../../components/Modal";
import { StateView } from "../../components/StateView";
import { useAsync } from "../../hooks/useAsync";
import { evolutionService } from "../../services/evolutionService";
import type { MetricEvolution, PatientMetricRequest } from "../../types/api";
import { formatDate } from "../../utils/format";

interface MetricForm {
  name: string;
  unit: string;
  description: string;
  visibleToFamily: boolean;
}

const EMPTY_METRIC: MetricForm = { name: "", unit: "", description: "", visibleToFamily: false };

export function EvolutionTab({ patientId }: Readonly<{ patientId: string }>) {
  const { data, loading, error, reload } = useAsync(
    () => evolutionService.evolution(patientId),
    [patientId],
  );
  const [metricModalOpen, setMetricModalOpen] = useState(false);
  const [metricForm, setMetricForm] = useState<MetricForm>(EMPTY_METRIC);
  const [formError, setFormError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  // Inline "add entry" state, keyed by metricId.
  const [entryFor, setEntryFor] = useState<string | null>(null);
  const [entryValue, setEntryValue] = useState("");
  const [entryDate, setEntryDate] = useState(new Date().toISOString().slice(0, 10));
  const [entryNotes, setEntryNotes] = useState("");

  async function handleCreateMetric(event: FormEvent) {
    event.preventDefault();
    if (!metricForm.name.trim()) {
      setFormError("El nombre es obligatorio");
      return;
    }
    const request: PatientMetricRequest = {
      name: metricForm.name.trim(),
      unit: metricForm.unit.trim() || null,
      description: metricForm.description.trim() || null,
      visibleToFamily: metricForm.visibleToFamily,
    };
    setSaving(true);
    setFormError(null);
    try {
      await evolutionService.createMetric(patientId, request);
      setMetricModalOpen(false);
      setMetricForm(EMPTY_METRIC);
      reload();
    } catch (err) {
      setFormError(err instanceof Error ? err.message : "No se pudo guardar");
    } finally {
      setSaving(false);
    }
  }

  function openEntry(metricId: string) {
    setEntryFor(metricId);
    setEntryValue("");
    setEntryDate(new Date().toISOString().slice(0, 10));
    setEntryNotes("");
  }

  async function submitEntry(event: FormEvent) {
    event.preventDefault();
    if (!entryFor) return;
    const numeric = Number(entryValue);
    if (Number.isNaN(numeric)) {
      globalThis.alert("El valor debe ser numérico");
      return;
    }
    try {
      await evolutionService.addEntry(entryFor, {
        value: numeric,
        entryDate,
        notes: entryNotes.trim() || null,
      });
      setEntryFor(null);
      reload();
    } catch (err) {
      globalThis.alert(err instanceof Error ? err.message : "Acción fallida");
    }
  }

  return (
    <div>
      <div className="page-header">
        <h2>Evolución</h2>
        <button className="btn btn--primary" onClick={() => setMetricModalOpen(true)}>
          Nueva métrica
        </button>
      </div>

      <StateView
        loading={loading}
        error={error}
        isEmpty={!data || data.length === 0}
        emptyMessage="Sin métricas de seguimiento definidas."
      >
        {data?.map((item) => (
          <MetricCard
            key={item.metric.id}
            item={item}
            onAddEntry={() => openEntry(item.metric.id)}
          />
        ))}
      </StateView>

      <Modal open={metricModalOpen} title="Nueva métrica" onClose={() => setMetricModalOpen(false)}>
        <form onSubmit={handleCreateMetric}>
          {formError && <div className="state state--error">{formError}</div>}
          <div className="form-grid">
            <label className="field">
              Nombre *
              <input
                value={metricForm.name}
                onChange={(e) => setMetricForm((f) => ({ ...f, name: e.target.value }))}
                required
              />
            </label>
            <label className="field">
              Unidad
              <input
                value={metricForm.unit}
                onChange={(e) => setMetricForm((f) => ({ ...f, unit: e.target.value }))}
                placeholder="p. ej. aciertos, %"
              />
            </label>
            <label className="field field--full">
              Descripción
              <textarea
                value={metricForm.description}
                onChange={(e) => setMetricForm((f) => ({ ...f, description: e.target.value }))}
                rows={2}
              />
            </label>
            <label className="field field--check">
              <input
                type="checkbox"
                checked={metricForm.visibleToFamily}
                onChange={(e) =>
                  setMetricForm((f) => ({ ...f, visibleToFamily: e.target.checked }))
                }
              />
              Visible para la familia
            </label>
          </div>
          <div className="modal__actions">
            <button
              type="button"
              className="btn btn--ghost"
              onClick={() => setMetricModalOpen(false)}
            >
              Cancelar
            </button>
            <button type="submit" className="btn btn--primary" disabled={saving}>
              {saving ? "Guardando…" : "Guardar"}
            </button>
          </div>
        </form>
      </Modal>

      <Modal open={entryFor !== null} title="Nuevo registro" onClose={() => setEntryFor(null)}>
        <form onSubmit={submitEntry}>
          <div className="form-grid">
            <label className="field">
              Valor *
              <input
                type="number"
                step="any"
                value={entryValue}
                onChange={(e) => setEntryValue(e.target.value)}
                required
              />
            </label>
            <label className="field">
              Fecha *
              <input
                type="date"
                value={entryDate}
                onChange={(e) => setEntryDate(e.target.value)}
                required
              />
            </label>
            <label className="field field--full">
              Notas
              <textarea
                value={entryNotes}
                onChange={(e) => setEntryNotes(e.target.value)}
                rows={2}
              />
            </label>
          </div>
          <div className="modal__actions">
            <button type="button" className="btn btn--ghost" onClick={() => setEntryFor(null)}>
              Cancelar
            </button>
            <button type="submit" className="btn btn--primary">
              Guardar
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
}

function MetricCard({
  item,
  onAddEntry,
}: Readonly<{ item: MetricEvolution; onAddEntry: () => void }>) {
  const { metric, entries } = item;
  return (
    <div className="card">
      <div className="page-header">
        <div>
          <h3>
            {metric.name}
            {metric.unit ? ` (${metric.unit})` : ""}
          </h3>
          {metric.description && <p className="text-muted">{metric.description}</p>}
        </div>
        <button className="btn btn--sm" onClick={onAddEntry}>
          Añadir registro
        </button>
      </div>
      {entries.length === 0 ? (
        <p className="text-muted">Sin registros todavía.</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>Fecha</th>
              <th>Valor</th>
              <th>Notas</th>
            </tr>
          </thead>
          <tbody>
            {entries.map((entry) => (
              <tr key={entry.id}>
                <td>{formatDate(entry.entryDate)}</td>
                <td>{entry.value}</td>
                <td className="text-muted">{entry.notes ?? "—"}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
