import { useState } from "react";
import { StateView } from "../components/StateView";
import { StatusBadge } from "../components/StatusBadge";
import { useAsync } from "../hooks/useAsync";
import { calendarService, stripeService } from "../services/integrationService";
import { humanizeEnum } from "../utils/format";

export function IntegrationsPage() {
  const stripe = useAsync(() => stripeService.status(), []);
  const calendar = useAsync(() => calendarService.status(), []);
  const [toast, setToast] = useState<string | null>(null);
  const [onboarding, setOnboarding] = useState(false);

  async function onboardConnect() {
    setOnboarding(true);
    try {
      const result = await stripeService.onboard();
      if (result.onboardingUrl) {
        setToast(`Enlace de alta generado: ${result.onboardingUrl}`);
      } else {
        setToast("La integración de Stripe está desactivada.");
      }
    } catch (err) {
      globalThis.alert(err instanceof Error ? err.message : "No se pudo generar el enlace");
    } finally {
      setOnboarding(false);
    }
  }

  return (
    <div>
      <div className="page-header">
        <h1>Integraciones</h1>
      </div>

      {toast && <div className="toast">{toast}</div>}

      <section className="card">
        <h2>Pagos (Stripe)</h2>
        <StateView loading={stripe.loading} error={stripe.error} isEmpty={!stripe.data}>
          {stripe.data && (
            <div className="detail-grid">
              <div>
                <span className="detail-label">Modo</span>
                <StatusBadge status={humanizeEnum(stripe.data.mode)} />
              </div>
              <div>
                <span className="detail-label">Estado</span>
                <StatusBadge status={stripe.data.enabled ? "Activo" : "Desactivado"} />
              </div>
              <div>
                <span className="detail-label">Comisión de plataforma</span>
                <span>{stripe.data.platformFeePercent}%</span>
              </div>
            </div>
          )}
        </StateView>
        <div className="btn-row">
          <button
            className="btn btn--primary"
            onClick={onboardConnect}
            disabled={onboarding || !stripe.data?.enabled}
          >
            {onboarding ? "Generando…" : "Configurar Stripe Connect"}
          </button>
        </div>
        {!stripe.data?.enabled && (
          <p className="hint">
            Stripe está desactivado. Defina las variables STRIPE_MODE y STRIPE_SECRET_KEY para
            activarlo.
          </p>
        )}
      </section>

      <section className="card">
        <h2>Calendario</h2>
        <StateView loading={calendar.loading} error={calendar.error} isEmpty={!calendar.data}>
          {calendar.data && (
            <div className="detail-grid">
              <div>
                <span className="detail-label">Proveedor</span>
                <StatusBadge status={humanizeEnum(calendar.data.provider)} />
              </div>
              <div>
                <span className="detail-label">Estado</span>
                <StatusBadge status={calendar.data.enabled ? "Activo" : "Desactivado"} />
              </div>
            </div>
          )}
        </StateView>
        <p className="hint">
          La exportación .ics está disponible siempre desde la agenda. La sincronización con Google
          u Outlook requiere configurar CALENDAR_PROVIDER y las credenciales del proveedor.
        </p>
      </section>
    </div>
  );
}
