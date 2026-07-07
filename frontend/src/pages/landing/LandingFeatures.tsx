const FEATURE_GROUPS = [
  {
    title: "Gestión clínica",
    items: [
      "Pacientes",
      "Tutores y familias",
      "Agenda",
      "Sesiones",
      "Objetivos terapéuticos",
      "Informes en PDF",
      "Consentimientos",
      "Documentos",
    ],
  },
  {
    title: "Portal familiar",
    items: [
      "Acceso familiar seguro",
      "Informes compartidos",
      "Documentos visibles",
      "Consentimientos",
      "Tareas para casa",
      "Evidencias de familia",
    ],
  },
  {
    title: "Administración",
    items: [
      "Usuarios y roles",
      "Clínicas",
      "Facturación",
      "Pagos",
      "Auditoría",
      "Notificaciones",
    ],
  },
];

/** Feature list grouped into clinical, family portal and administration areas. */
export function LandingFeatures() {
  return (
    <section className="landing-section landing-section--alt" id="funcionalidades">
      <div className="landing-section__head">
        <h2>Funcionalidades</h2>
        <p>Una plataforma completa para la gestión y la documentación clínica.</p>
      </div>

      <div className="landing-features">
        {FEATURE_GROUPS.map((group) => (
          <article className="landing-feature-group" key={group.title}>
            <h3>{group.title}</h3>
            <ul>
              {group.items.map((item) => (
                <li key={item}>{item}</li>
              ))}
            </ul>
          </article>
        ))}
      </div>
    </section>
  );
}
