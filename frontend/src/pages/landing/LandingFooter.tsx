import { Link } from "react-router-dom";

/** Public footer with product name, navigation and access links. */
export function LandingFooter() {
  const year = new Date().getFullYear();
  return (
    <footer className="landing-footer">
      <div className="landing-footer__grid">
        <div className="landing-footer__brand">
          <span className="landing-footer__logo">LogoPlus</span>
          <p>
            Plataforma de gestión clínica para logopedas, terapeutas y clínicas
            pequeñas.
          </p>
        </div>

        <nav className="landing-footer__col">
          <h4>Producto</h4>
          <a href="#producto">Beneficios</a>
          <a href="#funcionalidades">Funcionalidades</a>
          <a href="#precios">Precios</a>
          <a href="#faq">Preguntas frecuentes</a>
        </nav>

        <nav className="landing-footer__col">
          <h4>Acceso</h4>
          <Link to="/login?type=clinic">Acceder como clínica</Link>
          <Link to="/login?type=family">Acceder como familia</Link>
        </nav>
      </div>

      <div className="landing-footer__bottom">
        <span>© {year} LogoPlus</span>
        <span>
          Herramienta de gestión y documentación. No realiza diagnóstico ni
          recomendaciones clínicas.
        </span>
      </div>
    </footer>
  );
}
