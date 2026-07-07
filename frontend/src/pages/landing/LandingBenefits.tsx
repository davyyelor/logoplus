const BENEFITS = [
  {
    title: "Menos tiempo haciendo informes",
    body: "Plantillas y generación de PDF para documentar más rápido sin perder rigor.",
  },
  {
    title: "Información del paciente centralizada",
    body: "Historial, sesiones, objetivos y documentos siempre en un mismo lugar.",
  },
  {
    title: "Mejor comunicación con familias",
    body: "Un portal familiar seguro para compartir lo que la clínica decida.",
  },
  {
    title: "Consentimientos y documentos organizados",
    body: "Emite, consulta y firma consentimientos y ordena la documentación.",
  },
  {
    title: "Agenda, sesiones y objetivos conectados",
    body: "Todo el flujo clínico enlazado para reducir el trabajo administrativo.",
  },
  {
    title: "Preparado para clínicas y equipos",
    body: "Desde profesionales autónomos hasta centros con varios profesionales.",
  },
];

/** Grid of product benefits shown as cards. */
export function LandingBenefits() {
  return (
    <section className="landing-section" id="producto">
      <div className="landing-section__head">
        <h2>Todo lo que tu clínica necesita, sin complicaciones</h2>
        <p>
          LogoPlus reúne la operativa diaria de tu centro en una plataforma
          clara, pensada para el día a día terapéutico.
        </p>
      </div>

      <div className="landing-cards">
        {BENEFITS.map((benefit) => (
          <article className="landing-card" key={benefit.title}>
            <h3>{benefit.title}</h3>
            <p>{benefit.body}</p>
          </article>
        ))}
      </div>
    </section>
  );
}
