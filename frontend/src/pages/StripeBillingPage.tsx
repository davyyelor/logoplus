import { useState } from "react";
import type { FormEvent } from "react";
import { StateView } from "../components/StateView";
import { StatusBadge } from "../components/StatusBadge";
import { useAsync } from "../hooks/useAsync";
import { stripeService } from "../services/integrationService";
import type { CheckoutRequest } from "../types/api";
import { humanizeEnum } from "../utils/format";

interface FormState {
  patientId: string;
  description: string;
  amount: string;
}

const EMPTY_FORM: FormState = {
  patientId: "",
  description: "",
  amount: "",
};

export function StripeBillingPage() {
  const status = useAsync(() => stripeService.status(), []);
  const [form, setForm] = useState<FormState>(EMPTY_FORM);
  const [formError, setFormError] = useState<string | null>(null);
  const [checkoutUrl, setCheckoutUrl] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setFormError(null);
    setCheckoutUrl(null);
    const euros = Number(form.amount);
    if (!form.amount || Number.isNaN(euros) || euros <= 0) {
      setFormError("Introduce un importe válido en euros");
      return;
    }
    const request: CheckoutRequest = {
      patientId: form.patientId.trim() || null,
      description: form.description.trim() || null,
      amountCents: Math.round(euros * 100),
      currency: "EUR",
    };
    setSubmitting(true);
    try {
      const result = await stripeService.checkout(request);
      setCheckoutUrl(result.checkoutUrl);
      setForm(EMPTY_FORM);
    } catch (err) {
      setFormError(err instanceof Error ? err.message : "No se pudo crear el cobro");
    } finally {
      setSubmitting(false);
    }
  }

  const enabled = status.data?.enabled ?? false;

  return (
    <div>
      <div className="page-header">
        <h1>Cobros con Stripe</h1>
      </div>

      <section className="card">
        <StateView loading={status.loading} error={status.error} isEmpty={!status.data}>
          {status.data && (
            <div className="detail-grid">
              <div>
                <span className="detail-label">Modo</span>
                <StatusBadge status={humanizeEnum(status.data.mode)} />
              </div>
              <div>
                <span className="detail-label">Estado</span>
                <StatusBadge status={status.data.enabled ? "Activo" : "Desactivado"} />
              </div>
            </div>
          )}
        </StateView>
      </section>

      <section className="card">
        <h2>Nuevo cobro</h2>
        {!enabled && (
          <p className="hint">Stripe está desactivado. Actívalo en Ajustes → Integraciones.</p>
        )}
        <form onSubmit={handleSubmit}>
          <div className="form-grid">
            <label className="field">
              Importe (€) *
              <input
                type="number"
                min="0"
                step="0.01"
                value={form.amount}
                onChange={(e) => setForm((f) => ({ ...f, amount: e.target.value }))}
                required
              />
            </label>
            <label className="field">
              Paciente (opcional)
              <input
                value={form.patientId}
                onChange={(e) => setForm((f) => ({ ...f, patientId: e.target.value }))}
                placeholder="patientId"
              />
            </label>
            <label className="field field--full">
              Concepto
              <input
                value={form.description}
                onChange={(e) => setForm((f) => ({ ...f, description: e.target.value }))}
              />
            </label>
          </div>
          {formError && <p className="form-error">{formError}</p>}
          <div className="btn-row">
            <button type="submit" className="btn btn--primary" disabled={submitting || !enabled}>
              {submitting ? "Creando…" : "Crear enlace de cobro"}
            </button>
          </div>
        </form>
        {checkoutUrl && (
          <p className="hint">
            Enlace de pago:{" "}
            <a href={checkoutUrl} target="_blank" rel="noreferrer">
              {checkoutUrl}
            </a>
          </p>
        )}
      </section>
    </div>
  );
}
