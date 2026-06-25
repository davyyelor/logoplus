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
├── docker/       # init SQL de PostgreSQL
├── docker-compose.yml
└── .env          # configuración local de compose
```

- **Multi-tenant** por `clinicId` con aislamiento estricto en cada consulta.
- **Seguridad**: JWT stateless (jjwt), BCrypt, `@EnableMethodSecurity` con
  `@PreAuthorize` por endpoint. Roles: `CLINIC_ADMIN`, `THERAPIST`, `RECEPTION`,
  `FAMILY`.
- **Identificadores**: UUID (string de 36 caracteres) en todas las entidades V1.
- **PDF**: OpenPDF; los informes y consentimientos se renderizan a demanda y se
  transmiten como `application/pdf` (no se persiste el binario).
- **Persistencia dual**:
  - **Local / tests**: H2 en memoria (`MODE=PostgreSQL`, `create-drop`), Flyway
    desactivado, datos demo sembrados automáticamente.
  - **Docker**: PostgreSQL 16 + **Flyway** + `ddl-auto=validate`, esquema `app`.

---

## 2. Requisitos

| Herramienta | Versión              | Notas                                            |
| ----------- | -------------------- | ------------------------------------------------ |
| JDK         | 21 (Amazon Corretto) | Para ejecutar el backend en local                |
| Maven       | 3.9.x                | Build del backend                                |
| Node.js     | 18+                  | Build y dev server del frontend                  |
| Docker      | Desktop / Engine     | Para el despliegue con `docker-compose` (opcional)|

---

## 3. Ejecución en local (sin Docker)

### Backend (H2 en memoria, datos demo)

```powershell
cd backend
$env:JAVA_HOME = "C:\Program Files\Amazon Corretto\jdk21.0.11_10"
$env:Path = "C:\Users\<usuario>\.maven\maven-3.9.15\bin;$env:JAVA_HOME\bin;$env:Path"
mvn -q -DskipTests spring-boot:run
```

El backend arranca en `http://localhost:8080`. Al iniciar, `DemoDataSeeder` registra
en el log: `Demo data seeded: clinic=clinic-default, users=4, patients=2`.

### Frontend (Vite dev server)

```powershell
cd frontend
npm install        # primera vez
npm run dev
```

El frontend arranca en `http://localhost:5173` y hace proxy de `/api` a
`http://localhost:8080`. Abre `http://localhost:5173` e inicia sesión.

---

## 4. Ejecución con Docker (PostgreSQL + Flyway)

Requiere Docker en marcha. El fichero [.env](.env) ya trae valores por defecto locales.

```powershell
docker-compose up --build
```

Levanta tres servicios:

| Servicio   | Puerto local | Descripción                                        |
| ---------- | ------------ | -------------------------------------------------- |
| `postgres` | 5432         | PostgreSQL 16, crea el esquema `app` y extensiones |
| `backend`  | 8080         | Spring Boot con perfil `docker` (Flyway + validate)|
| `frontend` | 5173         | SPA de React servida por Vite                      |

En el perfil `docker`, Flyway aplica las migraciones `V1`–`V8` en el esquema `app` y
Hibernate las valida con `ddl-auto=validate`.

Para detener y limpiar:

```powershell
docker-compose down            # detener
docker-compose down -v         # detener y borrar el volumen de datos
```

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
```
