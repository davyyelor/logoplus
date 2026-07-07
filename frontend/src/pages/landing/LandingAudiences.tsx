import { Link } from "react-router-dom";

/**
 * Two audience-oriented sections: one for clinics/professionals and one for
 * families. Each links to the shared login with a contextual `type` param.
 */
export function LandingAudiences() {
  return (
    <section className="landing-section" id="audiencias">
      <div className="landing-audiences">
        <article className="landing-audience">
          <h2>Para clínicas y profesionales</h2>
          <p>
            Centraliza la operativa diaria de tu centro: agenda, pacientes,
            sesiones, informes, consentimientos, documentos, tareas y
            facturación. Diseñado para reducir trabajo administrativo sin
            sustituir el criterio profesional.
          </p>
          <Link className="landing-btn landing-btn--primary" to="/login?type=clinic">
            Entrar como clínica
          </Link>
        </article>

        <article className="landing-audience landing-audience--soft">
          <h2>Para familias</h2>
          <p>
            Accede de forma segura a la información compartida por tu clínica:
            próximas citas, informes, documentos, consentimientos, tareas para
            casa y comunicaciones importantes.
          </p>
          <Link className="landing-btn landing-btn--secondary" to="/login?type=family">
            Entrar como familia
          </Link>
        </article>
      </div>
    </section>
  );
}
