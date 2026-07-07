import { Link } from "react-router-dom";

/**
 * Public hero section. Presents the product value proposition and the two
 * primary access buttons (clinic / family), both routing to the existing login.
 */
export function LandingHero() {
  return (
    <header className="landing-hero" id="inicio">
      <nav className="landing-nav">
        <span className="landing-nav__brand">LogoPlus</span>
        <div className="landing-nav__links">
          <a href="#producto">Producto</a>
          <a href="#funcionalidades">Funcionalidades</a>
          <a href="#precios">Precios</a>
          <a href="#faq">FAQ</a>
        </div>
        <Link className="landing-btn landing-btn--ghost" to="/login?type=clinic">
          Iniciar sesión
        </Link>
      </nav>

      <div className="landing-hero__content">
        <span className="landing-hero__eyebrow">Software de gestión clínica</span>
        <h1 className="landing-hero__title">
          Gestiona tu clínica terapéutica desde un solo lugar
        </h1>
        <p className="landing-hero__subtitle">
          LogoPlus ayuda a logopedas, terapeutas y clínicas pequeñas a organizar
          pacientes, sesiones, informes, consentimientos, documentos, agenda,
          tareas para casa y comunicación con familias.
        </p>

        <div className="landing-hero__actions">
          <Link className="landing-btn landing-btn--primary" to="/login?type=clinic">
            Acceder como clínica
          </Link>
          <Link className="landing-btn landing-btn--secondary" to="/login?type=family">
            Acceder como familia
          </Link>
        </div>

        <p className="landing-hero__note">
          Sin IA clínica. Sin diagnóstico automático. La herramienta ayuda a
          documentar y organizar, pero las decisiones clínicas siempre las toma
          el profesional.
        </p>
      </div>
    </header>
  );
}
