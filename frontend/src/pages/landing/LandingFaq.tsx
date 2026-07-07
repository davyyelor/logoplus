const FAQS = [
  {
    q: "¿LogoPlus hace diagnóstico automático?",
    a: "No. LogoPlus no diagnostica ni recomienda tratamientos. Es una herramienta de gestión y documentación.",
  },
  {
    q: "¿Las familias pueden ver todos los datos?",
    a: "No. Las familias solo ven la información que la clínica comparte y únicamente de los pacientes vinculados.",
  },
  {
    q: "¿Puedo generar informes?",
    a: "Sí. LogoPlus permite trabajar con plantillas y generar informes en PDF.",
  },
  {
    q: "¿Sirve para logopedas autónomos?",
    a: "Sí. Está pensado tanto para profesionales independientes como para clínicas pequeñas.",
  },
  {
    q: "¿Puedo gestionar consentimientos?",
    a: "Sí. La plataforma permite emitir, consultar y firmar consentimientos según la funcionalidad disponible.",
  },
  {
    q: "¿Los precios son definitivos?",
    a: "No necesariamente. Son planes orientativos y pueden ajustarse según el tamaño de la clínica.",
  },
];

/** Frequently asked questions rendered as native disclosure blocks. */
export function LandingFaq() {
  return (
    <section className="landing-section" id="faq">
      <div className="landing-section__head">
        <h2>Preguntas frecuentes</h2>
        <p>Resolvemos las dudas más habituales sobre LogoPlus.</p>
      </div>

      <div className="landing-faq">
        {FAQS.map((item) => (
          <details className="landing-faq__item" key={item.q}>
            <summary>{item.q}</summary>
            <p>{item.a}</p>
          </details>
        ))}
      </div>
    </section>
  );
}
