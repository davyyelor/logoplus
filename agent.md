# AGENT.md — LogoPlus Public Landing Page

## Mission

Implement a public landing page for **LogoPlus**, a SaaS product for speech therapists, therapists, early childhood centers, child psychologists, occupational therapists and small clinics.

The landing page must communicate the product clearly, allow users to enter either as a clinic/professional or as a family/client, and document the pricing plans.

---

## Current product context

LogoPlus is a clinical management SaaS.

Current stack:

* Backend: Spring Boot 3.2.5, Java 21.
* Frontend: React 18, Vite, TypeScript.
* Auth: JWT.
* Roles:
  * `CLINIC_ADMIN`
  * `THERAPIST`
  * `RECEPTION`
  * `FAMILY`

Existing modules:

* Clinics.
* Users and roles.
* Patients.
* Guardians/families.
* Agenda.
* Sessions.
* Therapeutic goals.
* Report templates.
* PDF generation.
* Consents.
* Documents.
* Basic family portal.
* Billing.
* Notifications.
* Audit logs.

Important product positioning:

* LogoPlus does not perform clinical AI.
* LogoPlus does not diagnose.
* LogoPlus does not recommend therapies automatically.
* Professionals make all clinical decisions.
* The app supports documentation, organization and communication.

---

## Main objective

Create a public landing page available at:

```text
/
```

This route must be public and must not require authentication.

The existing authenticated application must remain available after login.

---

## Required landing structure

The landing page must include:

1. Hero section.
2. Product explanation section.
3. Benefits section.
4. Features section.
5. Section for clinics/professionals.
6. Section for families/clients.
7. Pricing section.
8. FAQ section.
9. Final CTA section.
10. Footer.

---

## Public access buttons

The landing must have two clear access buttons.

### Clinic access button

Text:

```text
Acceder como clínica
```

Preferred route:

```text
/login?type=clinic
```

Fallback route:

```text
/login
```

### Family access button

Text:

```text
Acceder como familia
```

Preferred route:

```text
/login?type=family
```

Fallback route:

```text
/login
```

Do not create separate login screens unless the current architecture already supports it naturally.

---

## Suggested copy

### Hero title

```text
Gestiona tu clínica terapéutica desde un solo lugar
```

### Hero subtitle

```text
LogoPlus ayuda a logopedas, terapeutas y clínicas pequeñas a organizar pacientes, sesiones, informes, consentimientos, documentos, agenda, tareas para casa y comunicación con familias.
```

### Clinical safety note

```text
Sin IA clínica. Sin diagnóstico automático. LogoPlus ayuda a documentar y organizar, pero las decisiones clínicas siempre las toma el profesional.
```

---

## Benefits section

Use cards or clean blocks.

Required benefits:

* Menos tiempo haciendo informes.
* Información del paciente centralizada.
* Mejor comunicación con familias.
* Consentimientos y documentos organizados.
* Agenda, sesiones y objetivos conectados.
* Preparado para clínicas pequeñas y equipos.

---

## Features section

Group features into three areas.

### Gestión clínica

* Pacientes.
* Tutores y familias.
* Agenda.
* Sesiones.
* Objetivos terapéuticos.
* Informes en PDF.
* Consentimientos.
* Documentos.

### Portal familiar

* Acceso familiar seguro.
* Informes compartidos.
* Documentos visibles.
* Consentimientos.
* Tareas para casa.
* Evidencias de familia.

### Administración

* Usuarios y roles.
* Clínicas.
* Facturación.
* Pagos.
* Auditoría.
* Notificaciones.

---

## Clinics/professionals section

Title:

```text
Para clínicas y profesionales
```

Text:

```text
Centraliza la operativa diaria de tu centro: agenda, pacientes, sesiones, informes, consentimientos, documentos, tareas y facturación. Diseñado para reducir trabajo administrativo sin sustituir el criterio profesional.
```

CTA:

```text
Entrar como clínica
```

Route:

```text
/login?type=clinic
```

---

## Families/clients section

Title:

```text
Para familias
```

Text:

```text
Accede de forma segura a la información compartida por tu clínica: próximas citas, informes, documentos, consentimientos, tareas para casa y comunicaciones importantes.
```

CTA:

```text
Entrar como familia
```

Route:

```text
/login?type=family
```

---

## Pricing section

Create a section named:

```text
Precios
```

The pricing plans must be easy to edit in code.

Prefer a constant/array such as:

```ts
const pricingPlans = [...]
```

Do not hardcode repeated markup manually if a data-driven structure is easy.

---

### Plan 1 — Profesional

Price:

```text
29 €/mes
```

Description:

```text
Para profesionales autónomos que quieren organizar pacientes, sesiones e informes.
```

Features:

* 1 usuario profesional.
* Hasta 40 pacientes activos.
* Agenda básica.
* Sesiones y objetivos.
* Informes PDF.
* Consentimientos.
* Portal familiar básico.
* Soporte por email.

CTA:

```text
Empezar con Profesional
```

---

### Plan 2 — Clínica

Price:

```text
79 €/mes
```

Description:

```text
Para clínicas pequeñas con varios profesionales y mayor volumen de pacientes.
```

Features:

* Hasta 5 usuarios.
* Hasta 200 pacientes activos.
* Agenda por profesionales.
* Informes y plantillas.
* Documentos y consentimientos.
* Portal familiar.
* Tareas para casa.
* Facturación.
* Soporte prioritario.

CTA:

```text
Empezar con Clínica
```

Mark this plan as:

```text
Recomendado
```

---

### Plan 3 — Centro

Price:

```text
149 €/mes
```

Description:

```text
Para centros con varios perfiles, mayor volumen operativo y necesidades avanzadas.
```

Features:

* Usuarios ampliados.
* Pacientes ampliados.
* Multi-centro.
* Exportación de historia.
* Cuestionarios.
* Evolución visual.
* Integraciones.
* Soporte avanzado.

CTA:

```text
Contactar
```

---

## Pricing disclaimer

Show this note below the plans:

```text
Precios orientativos. Los importes pueden ajustarse según volumen, número de profesionales e integraciones necesarias.
```

Do not implement real Stripe checkout as part of this task unless the project already has a fully configured Stripe flow.

Pricing CTAs can route to:

```text
/login
```

or, if registration exists:

```text
/auth/register-clinic-admin
```

For frontend routing, prefer:

```text
/login?plan=professional
/login?plan=clinic
/login?plan=center
```

Only use these query params if they do not break existing login behavior.

---

## FAQ section

Include these questions and answers.

### Question 1

```text
¿LogoPlus hace diagnóstico automático?
```

Answer:

```text
No. LogoPlus no diagnostica ni recomienda tratamientos. Es una herramienta de gestión y documentación.
```

### Question 2

```text
¿Las familias pueden ver todos los datos?
```

Answer:

```text
No. Las familias solo ven la información que la clínica comparte y únicamente de los pacientes vinculados.
```

### Question 3

```text
¿Puedo generar informes?
```

Answer:

```text
Sí. LogoPlus permite trabajar con plantillas y generar informes en PDF.
```

### Question 4

```text
¿Sirve para logopedas autónomos?
```

Answer:

```text
Sí. Está pensado tanto para profesionales independientes como para clínicas pequeñas.
```

### Question 5

```text
¿Puedo gestionar consentimientos?
```

Answer:

```text
Sí. La plataforma permite emitir, consultar y firmar consentimientos según la funcionalidad disponible.
```

### Question 6

```text
¿Los precios son definitivos?
```

Answer:

```text
No necesariamente. Son planes orientativos y pueden ajustarse según el tamaño de la clínica.
```

---

## Design requirements

The landing must be:

* Professional.
* Modern.
* Clean.
* Responsive.
* Suitable for a healthcare/therapy SaaS.
* Clear on desktop and mobile.
* Calm, trustworthy and not overly aggressive.

Recommended visual style:

* Soft background.
* Clear headings.
* Cards for benefits and pricing.
* Rounded containers.
* Good spacing.
* Clear CTAs.
* No clutter.
* No exaggerated medical claims.

Use the existing design system if the project has one.

If there is no existing design system, create maintainable CSS.

---

## Frontend implementation rules

Before coding:

1. Inspect the current frontend structure.
2. Find the router.
3. Find protected route logic.
4. Find the login page.
5. Check existing CSS/global styling approach.

Implementation:

* Add landing page as public route `/`.
* Keep login route public.
* Keep authenticated app routes protected.
* Do not break current auth flow.
* Do not remove existing pages.
* Do not duplicate large amounts of layout code unnecessarily.
* Use TypeScript types where useful.

Suggested component structure:

```text
src/pages/LandingPage.tsx
src/components/landing/LandingHero.tsx
src/components/landing/LandingBenefits.tsx
src/components/landing/LandingFeatures.tsx
src/components/landing/LandingPricing.tsx
src/components/landing/LandingFaq.tsx
src/components/landing/LandingFooter.tsx
```

If the current project structure is simpler, adapt to the existing conventions.

---

## Backend implementation rules

Backend changes should usually not be required.

Only touch backend if:

* The production SPA routing requires fallback configuration.
* Static serving needs adjustment.
* Existing security accidentally blocks public SPA entry.

Do not add new backend business logic for this task.

Do not implement billing APIs in this task.

---

## Acceptance criteria

The task is complete when:

* Visiting `/` displays the public landing page.
* No login is required to view `/`.
* “Acceder como clínica” routes to login.
* “Acceder como familia” routes to login.
* The pricing section displays the three plans.
* The landing is responsive.
* Existing login still works.
* Existing private routes remain protected.
* Frontend build passes.
* README mentions the new public landing page.
* No clinical AI or automatic diagnosis claims were added.
* No real payment integration was added unless already safely supported.

---

## Validation commands

Run:

```bash
cd frontend
npm install
npm run build
```

If backend is modified, also run:

```bash
cd backend
mvn test
```

---

## README update

Update the README with:

* New public landing page at `/`.
* Purpose of the landing.
* Access buttons for clinic and family.
* Pricing section.
* Note that prices are currently configurable/orientative.
* Note that the landing is public and the app remains protected behind login.

---

## Definition of done

A user opening the app for the first time should understand:

* What LogoPlus is.
* Who it is for.
* What problems it solves.
* What features it includes.
* How much it costs approximately.
* How to access as a clinic.
* How to access as a family.

Existing authenticated functionality must remain intact.
