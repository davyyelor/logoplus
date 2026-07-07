# LogoPlus — Plataforma de gestión clínica (MVP V1)

SaaS para **logopedas, terapeutas, centros de atención temprana, psicólogos infantiles,
terapeutas ocupacionales y clínicas pequeñas**. Permite gestionar la operativa clínica
del día a día: pacientes, agenda, sesiones terapéuticas, objetivos, informes con PDF,
consentimientos, documentos y un portal familiar básico. Incluye además el módulo de
**facturación** heredado de la V0 (pagos, tarifas y cobro de sesiones).

> **Alcance V1 (importante):** este producto **no** incluye IA clínica, diagnóstico
> automático ni recomendaciones terapéuticas automáticas. Todas las decisiones clínicas
> las toma el profesional. Las plantillas e informes son herramientas de apoyo
> documental, nunca generadores de criterio clínico.

---

## 1. Arquitectura

Monolito modular **Spring Boot 3.2.5 / Java 21** + frontend **React 18 + Vite + TypeScript**.

```
logoplus/
├── backend/      # Spring Boot (API REST, seguridad JWT, JPA, Flyway, PDF)
├── frontend/     # React + Vite + TypeScript (SPA)
├── docker/       # init SQL legacy de PostgreSQL (opcional, self-hosted)
├── docker-compose.legacy.yml  # PostgreSQL local (DEPRECADO, ya no es necesario)
└── .env.example  # plantilla de variables de entorno (copiar a .env)
```

- **Multi-tenant** por `clinicId` con aislamiento estricto en cada consulta.
- **Seguridad**: JWT stateless (jjwt), BCrypt, `@EnableMethodSecurity` con
  `@PreAuthorize` por endpoint. Roles: `CLINIC_ADMIN`, `THERAPIST`, `RECEPTION`,
  `FAMILY`.
- **Identificadores**: UUID (string de 36 caracteres) en todas las entidades V1.
- **PDF**: OpenPDF; los informes y consentimientos se renderizan a demanda y se
  transmiten como `application/pdf` (no se persiste el binario).
- **Persistencia**:
  - **Principal**: **Supabase PostgreSQL**. **Flyway** crea/actualiza las tablas
    en el esquema `app` y Hibernate solo valida (`ddl-auto=validate`).
  - **Fallback local/tests**: **H2** en memoria (`MODE=PostgreSQL`, `create-drop`,
    Flyway desactivado, datos demo sembrados). Se usa automáticamente cuando
    Supabase no está configurado o no es accesible.
  - La selección es **automática** al arrancar (ver sección 4).

---

## 2. Requisitos

| Herramienta | Versión              | Notas                                            |
| ----------- | -------------------- | ------------------------------------------------ |
| JDK         | 21 (Amazon Corretto) | Para ejecutar el backend en local                |
| Maven       | 3.9.x                | Build del backend                                |
| Node.js     | 18+                  | Build y dev server del frontend                  |
| Supabase    | Proyecto PostgreSQL  | Base de datos principal (opcional para dev con H2)|

> **Docker ya no es necesario.** La base de datos principal es Supabase y el
> fallback local es H2 en memoria. El fichero `docker-compose.legacy.yml` se
> conserva solo para quien prefiera un PostgreSQL local autogestionado.

---

## 3. Ejecución en local

### Backend con H2 (fallback, sin dependencias externas)

Sin variables de Supabase, el backend usa **H2 en memoria** con datos demo. Es el
modo más rápido para desarrollar.

```powershell
cd backend
$env:JAVA_HOME = "C:\Program Files\Amazon Corretto\jdk21.0.11_10"
$env:Path = "C:\Users\<usuario>\.maven\maven-3.9.15\bin;$env:JAVA_HOME\bin;$env:Path"

# Perfil H2 explícito + datos demo
$env:SPRING_PROFILES_ACTIVE = "h2"
$env:APP_SEED_DEMO_DATA = "true"
mvn -q -DskipTests spring-boot:run
```

El backend arranca en `http://localhost:8080`. Al iniciar, `DemoDataSeeder` registra
en el log: `Demo data seeded: clinic=clinic-default, users=4, patients=2`.

### Backend con Supabase (base de datos principal)

Define las variables de Supabase (nunca con la contraseña real en el repositorio) y
arranca. Flyway creará/actualizará las tablas en el esquema `app`.

```powershell
cd backend
$env:JAVA_HOME = "C:\Program Files\Amazon Corretto\jdk21.0.11_10"
$env:Path = "C:\Users\<usuario>\.maven\maven-3.9.15\bin;$env:JAVA_HOME\bin;$env:Path"

$env:SPRING_PROFILES_ACTIVE = "supabase"
$env:SUPABASE_DB_URL = "jdbc:postgresql://db.zxxrjvceoobnclgdckwh.supabase.co:5432/postgres?sslmode=require&currentSchema=app"
$env:SUPABASE_DB_USERNAME = "postgres"
$env:SUPABASE_DB_PASSWORD = "<tu-password-de-supabase>"   # nunca lo subas al repo
$env:APP_DB_FALLBACK_TO_H2 = "true"                        # dev: cae a H2 si Supabase no responde
$env:APP_SEED_DEMO_DATA = "false"
mvn spring-boot:run
```

> También puedes omitir `SPRING_PROFILES_ACTIVE`: si defines `SUPABASE_DB_URL`/
> `SUPABASE_DB_PASSWORD` y Supabase responde, el perfil `supabase` se activa solo.

### Frontend (Vite dev server)

```powershell
cd frontend
npm install        # primera vez
npm run dev
```

El frontend arranca en `http://localhost:5173` y hace proxy de `/api` a
`http://localhost:8080`. Abre `http://localhost:5173` e inicia sesión.

---

## 4. Base de datos: Supabase (principal) y H2 (fallback)

La base de datos principal es **Supabase PostgreSQL**. **H2** en memoria es el
fallback para desarrollo local y tests. La elección es **automática** al arrancar
(la decide `DatabaseFallbackEnvironmentPostProcessor`):

1. **Supabase configurado y accesible** → perfil `supabase`: Flyway aplica las
   migraciones en el esquema `app` y Hibernate valida (`ddl-auto=validate`).
2. **Supabase no configurado** → H2 en memoria (perfil por defecto).
3. **Supabase configurado pero sin conexión**:
   - `APP_DB_FALLBACK_TO_H2=true` (por defecto) → cae a H2 (solo dev local).
   - `APP_DB_FALLBACK_TO_H2=false` → el arranque falla con un error claro (prod).

### 4.1 Variables de entorno

Copia `.env.example` a `.env` (git-ignorado) y rellena los valores **en local**.

| Variable | Ejemplo / por defecto | Descripción |
| -------- | --------------------- | ----------- |
| `SUPABASE_DB_URL` | `jdbc:postgresql://db.<ref>.supabase.co:5432/postgres?sslmode=require&currentSchema=app` | URL JDBC de la base de datos de Supabase. |
| `SUPABASE_DB_USERNAME` | `postgres` | Usuario de la base de datos. |
| `SUPABASE_DB_PASSWORD` | *(secreto)* | Contraseña de Supabase. **Nunca** se sube al repositorio. |
| `APP_DB_FALLBACK_TO_H2` | `true` | `true` cae a H2 si Supabase falla (dev); `false` falla claramente (prod). |
| `APP_SEED_DEMO_DATA` | `false` | Siembra datos demo. Mantener `false` contra una base de datos real. |
| `SPRING_PROFILES_ACTIVE` | *(auto)* | Fuerza `supabase` o `h2`. Si se omite, se detecta automáticamente. |

> ⚠️ **Nunca subas contraseñas reales al repositorio.** `.env`, `.env.local` y
> `.env.prod` están en `.gitignore`. Solo `.env.example` (con placeholders) se versiona.

### 4.2 Crear/actualizar las tablas en Supabase con Flyway

Arranca el backend con el perfil `supabase` (sección 3). Al iniciar, Flyway aplica
las migraciones `V1`–`V12` sobre el esquema `app` (`create-schemas: true` crea el
esquema si no existe). Las migraciones `V9`–`V12` son de la **Versión 2** (sección 11)
y son siempre aditivas.

### 4.3 Comprobar las tablas creadas

Desde el editor SQL de Supabase (o cualquier cliente PostgreSQL):

```sql
-- Tablas creadas en el esquema de la aplicación
select table_schema, table_name
from information_schema.tables
where table_schema = 'app'
order by table_name;

-- Historial de migraciones aplicadas por Flyway
select * from app.flyway_schema_history order by installed_rank;
```

### 4.4 PostgreSQL local (opcional, DEPRECADO)

Docker ya no es necesario. Si prefieres un PostgreSQL local autogestionado,
renombra `docker-compose.legacy.yml` a `docker-compose.yml`, levántalo y apunta
`SUPABASE_DB_URL` a `jdbc:postgresql://localhost:5432/...`.

---

## 5. Usuarios demo

Sembrados automáticamente cuando `app.seed-demo-data=true` (activo en local y tests).
Todos comparten la contraseña **`Demo1234!`**.

| Email                  | Rol            | Para qué sirve                                   |
| ---------------------- | -------------- | ------------------------------------------------ |
| `admin@demo.local`     | `CLINIC_ADMIN` | Acceso total: clínica, usuarios, pacientes, etc. |
| `therapist@demo.local` | `THERAPIST`    | Sesiones, objetivos, informes                    |
| `reception@demo.local` | `RECEPTION`    | Pacientes, agenda, consentimientos               |
| `family@demo.local`    | `FAMILY`       | Portal familiar (solo paciente vinculado)        |

Datos demo: clínica `clinic-default` ("Centro LogoPlus Demo"), 2 pacientes
(`patient-001` Lucía García, `patient-002` Mateo Fernández), 1 tutor que vincula a
`family@demo.local` con `patient-001`, 1 plantilla de informe y 1 de consentimiento.

---

## 6. Prueba del flujo completo V1

1. **Login staff**: entra como `admin@demo.local` / `Demo1234!`.
2. **Panel**: revisa KPIs (pacientes activos, citas de hoy, cobro pendiente).
3. **Pacientes** → abre **Lucía García** (`/patients/patient-001`).
   - **Tutores**: edita el tutor existente o añade uno; usa "Crear acceso" para
     generar credenciales de portal familiar (contraseña temporal).
   - **Agenda**: crea una cita (selecciona terapeuta, fecha/hora, modalidad).
   - **Sesiones**: registra una sesión terapéutica (resumen, actividades, tareas).
   - **Objetivos**: crea un objetivo terapéutico y márcalo como "Conseguido".
   - **Informes**: genera un informe a partir de la plantilla, descárgalo en PDF y
     pulsa "Compartir con familia".
   - **Documentos**: sube un documento y márcalo como visible para la familia.
   - **Consentimientos**: emite un consentimiento a partir de la plantilla.
4. **Logout** y **login** como `family@demo.local` / `Demo1234!`.
5. **Portal familiar** (`/portal`): comprueba que **solo** ves a Lucía García,
   con sus citas, sesiones, informes compartidos, documentos visibles y
   consentimientos (que puedes firmar).
6. **Aislamiento**: el portal familiar nunca expone pacientes no vinculados; los
   endpoints de staff devuelven `403` para el rol `FAMILY` (cubierto por tests).

---

## 7. Pruebas automatizadas

```powershell
cd backend
mvn test
```

Incluye, además de los tests de facturación V0, `V1SecurityIntegrationTest`, que
valida de extremo a extremo (a través de la cadena de seguridad real):

- Login y emisión de JWT.
- `CLINIC_ADMIN` ve los 2 pacientes.
- `FAMILY` ve **solo** su paciente vinculado vía `/api/family/me/patients`.
- `FAMILY` recibe `403` en `/api/patients` (aislamiento).
- Credenciales incorrectas → `401`; petición sin token → `401`.

---

## 8. Principales endpoints de la API

Base: `/api`. Autenticación: `Authorization: Bearer <token>` salvo `/auth/login`.

| Área              | Endpoints                                                                 |
| ----------------- | ------------------------------------------------------------------------- |
| Auth              | `POST /auth/login`, `POST /auth/register-clinic-admin`, `GET /auth/me`    |
| Clínica           | `GET /clinics/current`, `PUT /clinics/current`                            |
| Usuarios          | `GET/POST /users`, `PUT /users/{id}`, `PATCH /users/{id}/activate|deactivate` |
| Pacientes         | `GET/POST /patients`, `GET/PUT /patients/{id}`, `PATCH /patients/{id}/status` |
| Tutores           | `GET/POST /patients/{id}/guardians`, `PUT/DELETE /guardians/{id}`, `POST /guardians/{id}/create-family-access` |
| Agenda            | `GET/POST /appointments`, `PUT /appointments/{id}`, `PATCH .../cancel|complete|no-show` |
| Sesiones          | `GET/POST /sessions`, `PUT/DELETE /sessions/{id}`                          |
| Objetivos         | `GET/POST /patients/{id}/goals`, `PUT /goals/{id}`, `PATCH /goals/{id}/status` |
| Informes          | `GET /patients/{id}/reports`, `POST .../reports/generate`, `PATCH /reports/{id}/share-with-family|archive`, `GET /reports/{id}/pdf` |
| Plantillas inf.   | `GET/POST /report-templates`, `PUT`, `PATCH .../activate|deactivate`       |
| Consentimientos   | `GET /patients/{id}/consents`, `POST` (emitir), `PATCH /consents/{id}/sign|revoke`, `GET /consents/{id}/pdf` |
| Plantillas cons.  | `GET/POST /consent-templates`, `PUT`, `PATCH .../activate|deactivate`       |
| Documentos        | `GET /patients/{id}/documents`, `POST` (multipart), `GET /documents/{id}/download`, `PATCH .../share-with-family|hide-from-family` |
| Notificaciones    | `GET /notifications`, `GET /notifications/unread-count`, `PATCH .../read`   |
| Auditoría         | `GET /audit-logs` (solo `CLINIC_ADMIN`)                                    |
| Portal familiar   | `GET /family/me/patients`, `GET /family/patients/{id}/appointments|sessions|documents|reports|consents`, `POST .../documents`, `PATCH /family/consents/{id}/sign` |
| Facturación (V0)  | `/payments`, `/fees`, `/session-billing`, `/billing/summary`, `/exports/*.csv` |

---

## 9. Limitaciones conocidas (V1)

- Sin IA clínica, diagnóstico ni recomendaciones automáticas (por diseño).
- El binario PDF no se persiste; se regenera a demanda (campo `pdfDocumentId` queda
  `null`).
- Integraciones de facturación de terceros (Holded, Quipu, Stripe, TicketBAI,
  Verifactu) están cableadas como *placeholders* desactivados; no llaman a APIs reales.
- Las notificaciones se generan/almacenan pero no se envían por email/SMS/push.
- Una clínica activa por defecto (`clinic-default`); el alta self-service de nuevas
  clínicas existe vía `POST /auth/register-clinic-admin` pero sin onboarding guiado.
- Almacenamiento de documentos en sistema de ficheros local (abstracción `StoragePort`
  preparada para migrar a S3).

---

## 10. Próximos pasos (V1.1)

- Recordatorios de cita por email/SMS y centro de notificaciones en la UI.
- Onboarding guiado de clínica y gestión de suscripción/planes.
- Persistencia opcional del PDF y firma electrónica avanzada de consentimientos.
- Búsqueda y filtros avanzados; calendario semanal/mensual de agenda.
- Almacenamiento de documentos en S3 y antivirus en la subida.
- Activación real de integraciones de facturación electrónica (TicketBAI/Verifactu).
- App móvil del portal familiar (la carpeta `mobile/` está reservada para ello).

> Buena parte de esta lista ya se ha entregado en la **Versión 2** (ver sección 11).

---

## 11. Versión 2 (V2)

La V2 amplía la plataforma en 5 fases, **sin romper la V1**: se mantiene el aislamiento
estricto por `clinicId`, el rol `FAMILY` sólo accede a pacientes vinculados, **no** se
introduce IA clínica ni automatización diagnóstica, y todas las tablas nuevas se crean
con Flyway a partir de `V9`. Cualquier integración externa vive detrás de una
interfaz/puerto y puede funcionar en modo `disabled`/`mock`, por lo que **no** contacta
servicios externos salvo que se active explícitamente con credenciales válidas.

### 11.1 Alcance por fases

| Fase | Área | Contenido |
| ---- | ---- | --------- |
| **P1** | Colaboración con familias | Tareas para casa, evidencias de la familia, métricas de evolución, cuestionarios y ampliación del portal familiar. |
| **P2** | Recordatorios y exportación | Recordatorios de cita (`@Scheduled`) y exportación de la historia clínica (ZIP/PDF/CSV). |
| **P3** | Multi-centro | Entidad `Center` y `centerId` opcional en pacientes, usuarios, citas y sesiones. |
| **P4** | Pagos y calendario | Stripe (Connect + Checkout + webhooks) y proveedores de calendario (Google/Outlook) + exportación `.ics`, todo tras puertos en modo `disabled`/`mock`. |
| **P5** | Firma electrónica | `SignatureProviderPort` con firma interna básica (hash SHA-256 verificable) y placeholder avanzado desactivado; portal de firmas para la familia. |

### 11.2 Módulos backend nuevos

- `com.logopeda.homework`, `evidence`, `evolution`, `questionnaire` — colaboración con familias (P1).
- `com.logopeda.reminder`, `clinicalhistory` — recordatorios programados y exportación (P2).
- `com.logopeda.center` — soporte multi-centro (P3).
- `com.logopeda.paymentgateway` — pasarela Stripe tras `StripePaymentPort` (P4).
- `com.logopeda.calendar` — sincronización de calendario tras `CalendarProviderPort` + `.ics` (P4).
- `com.logopeda.signature` — firma electrónica tras `SignatureProviderPort` (P5).

### 11.3 Endpoints V2 destacados

| Área | Endpoints |
| ---- | --------- |
| Tareas para casa | `GET/POST /patients/{id}/homework`, `PATCH /homework/{id}/status`, `GET /family/patients/{id}/homework` |
| Evidencias familia | `POST /family/patients/{id}/evidence`, `POST /family/patients/{id}/evidence/upload` |
| Evolución | `GET/POST /patients/{id}/evolution`, `GET /family/patients/{id}/evolution` |
| Cuestionarios | `GET/POST /questionnaire-templates`, asignación y `GET /family/questionnaires` |
| Recordatorios | `GET /reminders`, `GET /family/reminders` (generación programada con `@Scheduled`) |
| Historia clínica | `GET /patients/{id}/clinical-history/export` (ZIP/PDF/CSV) |
| Centros | `GET/POST /centers`, `PUT /centers/{id}`, `PATCH /centers/{id}/activate\|deactivate` |
| Stripe | `GET /billing/stripe/status`, `POST /billing/stripe/checkout`, `POST /billing/stripe/connect/onboard`, `POST /webhooks/stripe` (público) |
| Calendario | `GET /calendar/status`, `GET /appointments/{id}/calendar.ics`, `POST /appointments/{id}/calendar/sync` |
| Firmas | `GET /signatures/status`, `GET/POST /patients/{id}/signatures`, `GET /signatures/{id}`, `GET /family/signatures/status`, `GET/POST /family/patients/{id}/signatures` |

### 11.4 Variables de entorno opcionales (integraciones)

Todas tienen valores por defecto seguros; con ellos las integraciones quedan
**desactivadas** (o en modo interno básico) y no realizan llamadas externas.

| Variable | Por defecto | Descripción |
| -------- | ----------- | ----------- |
| `STRIPE_MODE` | `disabled` | `disabled` o `mock` (sin SDK real en esta entrega). |
| `STRIPE_SECRET_KEY` | *(vacío)* | Clave secreta de Stripe (sólo en modo real futuro). |
| `STRIPE_WEBHOOK_SECRET` | *(vacío)* | Secreto de verificación de webhooks. |
| `STRIPE_PLATFORM_FEE_PERCENT` | `0` | Comisión de plataforma (Connect). |
| `CALENDAR_PROVIDER` | `disabled` | `disabled`, `google` u `outlook`. |
| `GOOGLE_CALENDAR_CLIENT_ID` / `GOOGLE_CALENDAR_CLIENT_SECRET` | *(vacío)* | Credenciales OAuth de Google Calendar. |
| `OUTLOOK_CALENDAR_CLIENT_ID` / `OUTLOOK_CALENDAR_CLIENT_SECRET` | *(vacío)* | Credenciales OAuth de Outlook/Microsoft 365. |
| `SIGNATURE_PROVIDER` | `internal-basic` | `internal-basic` (firma interna) o `advanced-disabled` (placeholder externo). |

> El puerto `/api/webhooks/stripe` es **público** (lo consume Stripe) y en modo
> `disabled`/`mock` responde `200` con `handled=false`. El proveedor de firma
> `advanced-disabled` reporta `enabled=false` y rechaza firmar (`400`).

### 11.5 Tests de seguridad y multi-tenant V2

Además de los tests V1 y de facturación, la V2 añade tests de integración de seguridad
que se ejecutan a través de la cadena de seguridad real (`mvn test`):

- `V2Phase3SecurityIntegrationTest` — CRUD de centros por rol, aislamiento y validación de `centerId`.
- `V2Phase4SecurityIntegrationTest` — estado de Stripe/calendario, checkout rechazado en `disabled`, webhook público, `403` para `FAMILY`, `.ics` inexistente `404`.
- `V2Phase5SecurityIntegrationTest` — firma por staff y por familia (paciente vinculado), rechazo de firma sobre paciente no vinculado (`403`), `403` de `FAMILY` en endpoints de staff, `401` sin token y `404` en paciente inexistente.

El frontend compila con `npm run build` (`tsc -b && vite build`) y el backend con
`mvn test`.

---

## 12. Landing pública

El frontend expone una **landing page pública** en la ruta `/`, accesible **sin
autenticación**, orientada a presentar el producto a clínicas, profesionales y
familias. La aplicación privada sigue protegida y el flujo de login no cambia.

### 12.1 Rutas

| Ruta | Acceso | Contenido |
| ---- | ------ | --------- |
| `/` | Pública | Landing de marketing (hero, beneficios, funcionalidades, precios, FAQ, CTA, footer). |
| `/login` | Pública | Login existente; tolera `?type=clinic` y `?type=family` (solo cambia el subtítulo, no la lógica de autenticación). |
| `/dashboard` | `CLINIC_ADMIN`, `THERAPIST`, `RECEPTION` | Panel del staff (antes en `/`). |
| `/portal` | `FAMILY` | Portal familiar. |

Tras iniciar sesión, el staff aterriza en `/dashboard` y las familias en `/portal`.
Las rutas privadas siguen protegidas por `ProtectedRoute`; los usuarios no
autenticados que accedan a ellas son redirigidos a `/login`.

### 12.2 Botones de acceso

- **Acceder como clínica** → `/login?type=clinic`
- **Acceder como familia** → `/login?type=family`

Ambos usan el login actual. No se ha creado un login separado; solo se prepara la
navegación con el parámetro `type`.

### 12.3 Componentes

Landing modular en [frontend/src/pages/landing](frontend/src/pages/landing):

- `LandingPage.tsx` (composición) + `landing.css` (estilos propios, responsive).
- `LandingHero`, `LandingBenefits`, `LandingFeatures`, `LandingAudiences`,
  `LandingPricing`, `LandingFaq`, `LandingCta`, `LandingFooter`.

Los tres planes de precios (**Profesional 29 €/mes**, **Clínica 79 €/mes** —
recomendado— y **Centro 149 €/mes**) son orientativos y editables desde
`LandingPricing.tsx`. No hay integración de pagos en la landing: los CTA llevan al
login. La landing no incluye claims médicos ni IA clínica.
