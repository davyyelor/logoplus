import { Link } from "react-router-dom";

/** Final call-to-action inviting clinics and families to access the platform. */
export function LandingCta() {
  return (
    <section className="landing-cta" id="empezar">
      <h2>Empieza a organizar tu clínica con LogoPlus</h2>
      <p>
        Centraliza pacientes, sesiones, informes y comunicación con familias en
        una sola plataforma clara y segura.
      </p>
      <div className="landing-cta__actions">
        <Link className="landing-btn landing-btn--light" to="/login?type=clinic">
          Acceder como clínica
        </Link>
        <Link className="landing-btn landing-btn--light-outline" to="/login?type=family">
          Acceder como familia
        </Link>
      </div>
    </section>
  );
}
