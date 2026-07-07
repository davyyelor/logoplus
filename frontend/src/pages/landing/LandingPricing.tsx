import { Link } from "react-router-dom";

interface Plan {
  name: string;
  price: string;
  cadence: string;
  description: string;
  features: string[];
  cta: string;
  ctaTo: string;
  recommended?: boolean;
}

// Editable pricing plans. Prices are indicative and can be adjusted here.
const PLANS: Plan[] = [
  {
    name: "Profesional",
    price: "29 €",
    cadence: "/mes",
    description:
      "Para profesionales autónomos que quieren organizar pacientes, sesiones e informes.",
    features: [
      "1 usuario profesional",
      "Hasta 40 pacientes activos",
      "Agenda básica",
      "Sesiones y objetivos",
      "Informes PDF",
      "Consentimientos",
      "Portal familiar básico",
      "Soporte por email",
    ],
    cta: "Empezar con Profesional",
    ctaTo: "/login?type=clinic",
  },
  {
    name: "Clínica",
    price: "79 €",
    cadence: "/mes",
    description:
      "Para clínicas pequeñas con varios profesionales y mayor volumen de pacientes.",
    features: [
      "Hasta 5 usuarios",
      "Hasta 200 pacientes activos",
      "Agenda por profesionales",
      "Informes y plantillas",
      "Documentos y consentimientos",
      "Portal familiar",
      "Tareas para casa",
      "Facturación",
      "Soporte prioritario",
    ],
    cta: "Empezar con Clínica",
    ctaTo: "/login?type=clinic",
    recommended: true,
  },
  {
    name: "Centro",
    price: "149 €",
    cadence: "/mes",
    description:
      "Para centros con varios perfiles, mayor volumen operativo y necesidades avanzadas.",
    features: [
      "Usuarios ampliados",
      "Pacientes ampliados",
      "Multi-centro",
      "Exportación de historia",
      "Cuestionarios",
      "Evolución visual",
      "Integraciones",
      "Soporte avanzado",
    ],
    cta: "Contactar",
    ctaTo: "/login?type=clinic",
  },
];

/** Pricing section with three editable plans. */
export function LandingPricing() {
  return (
    <section className="landing-section landing-section--alt" id="precios">
      <div className="landing-section__head">
        <h2>Precios</h2>
        <p>Planes pensados para crecer contigo, desde autónomos hasta centros.</p>
      </div>

      <div className="landing-pricing">
        {PLANS.map((plan) => (
          <article
            className={`landing-plan${plan.recommended ? " landing-plan--recommended" : ""}`}
            key={plan.name}
          >
            {plan.recommended && <span className="landing-plan__badge">Recomendado</span>}
            <h3 className="landing-plan__name">{plan.name}</h3>
            <p className="landing-plan__price">
              {plan.price}
              <span className="landing-plan__cadence">{plan.cadence}</span>
            </p>
            <p className="landing-plan__description">{plan.description}</p>
            <ul className="landing-plan__features">
              {plan.features.map((feature) => (
                <li key={feature}>{feature}</li>
              ))}
            </ul>
            <Link
              className={`landing-btn ${plan.recommended ? "landing-btn--primary" : "landing-btn--outline"}`}
              to={plan.ctaTo}
            >
              {plan.cta}
            </Link>
          </article>
        ))}
      </div>

      <p className="landing-pricing__note">
        Precios orientativos. Los importes pueden ajustarse según volumen, número
        de profesionales e integraciones necesarias.
      </p>
    </section>
  );
}
