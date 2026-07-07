# Integración Stripe para SaaS multi-clínica (Logopedia)

## 1) Decisión recomendada

### Opción principal
Usar **Stripe Connect + Checkout Sessions** con enfoque de **Destination charges** para que cada clínica reciba su dinero en su cuenta conectada, sin reparto manual posterior.

### Por qué encaja mejor
- Cada clínica cobra en su propia cuenta conectada.
- La plataforma mantiene control centralizado (trazabilidad, auditoría, conciliación).
- Facilita comisiones futuras de plataforma (si aplica según configuración legal/técnica).
- Checkout reduce complejidad PCI/SCA frente a un flujo totalmente custom.

### Comparativa rápida de modelos solicitados
1. **Stripe normal (sin Connect)**: no recomendado para multi-clínica con cobro por clínica.
2. **Stripe Connect**: sí, es la base correcta.
3. **Direct charges**: viable, pero menos control central en algunos escenarios SaaS.
4. **Destination charges**: recomendado para este caso.
5. **Separate charges and transfers**: más flexible, pero más complejo operativa/contablemente.
6. **Checkout Sessions**: recomendado para MVP por seguridad y rapidez.
7. **PaymentIntents**: útil para UX avanzada; puede incorporarse después.

> Nota: si algún parámetro exacto de API cambia por versión, validar siempre en docs oficiales de Stripe.

---

## 2) Arquitectura general

### Componentes
- **Frontend (Vercel)**: solicita creación de pago y redirige a Checkout.
- **Backend (Spring Boot/Java)**: crea cuentas conectadas, onboarding, checkout sessions, procesa webhooks, reembolsos.
- **DB relacional**: fuente de verdad interna (facturas/pagos/eventos procesados).
- **Stripe**: fuente de verdad externa para estado real del pago.

### Principios obligatorios
- Multi-tenant estricto por `clinicId`.
- No confiar en frontend para confirmar pagos.
- Firma de webhook siempre validada.
- Idempotencia en webhooks y operaciones críticas.
- Claves secretas solo en backend y por entorno.

### Diagrama textual
1. Usuario/recepción clínica crea factura en la app.
2. Backend crea sesión de pago en Stripe (asociada a clínica conectada).
3. Paciente paga en Checkout.
4. Stripe envía webhook firmado.
5. Backend valida, aplica idempotencia y actualiza `Payment` + `Invoice`.

---

## 3) Flujo paso a paso

### 3.1 Registro de clínica y cuenta conectada
1. Crear `Clinic` en BD.
2. Crear (o reutilizar) cuenta conectada en Stripe Connect.
3. Guardar `stripeAccountId`.
4. Generar onboarding link.
5. Redirigir responsable de clínica a Stripe.
6. Consultar estado de onboarding/capabilities.
7. Marcar clínica como apta para cobros solo cuando Stripe lo confirme.

### 3.2 Onboarding
- `POST /api/clinics/{clinicId}/stripe/onboarding-link`
  - Valida permisos.
  - Crea/reutiliza cuenta conectada.
  - Devuelve URL de onboarding.
- `GET /api/clinics/{clinicId}/stripe/status`
  - Devuelve estado de onboarding, charges/payouts enabled y requisitos pendientes.

### 3.3 Creación de pago (factura/sesión)
1. Frontend llama `POST /api/invoices/{invoiceId}/checkout-session`.
2. Backend valida:
   - Usuario autorizado para la clínica.
   - Factura pertenece a la clínica.
   - Estado de factura pagable.
   - Clínica con cuenta conectada activa.
3. Crear `Payment` interno en estado inicial.
4. Crear Checkout Session en Stripe con metadata (`clinicId`, `invoiceId`, `patientId`, `tenantId`, `paymentId`).
5. Guardar IDs de Stripe (`stripeCheckoutSessionId`, `stripePaymentIntentId` si aplica).
6. Devolver URL de Checkout al frontend.
7. Frontend redirige al paciente.

### 3.4 Confirmación por webhook
1. Stripe llama `POST /api/stripe/webhook`.
2. Leer body raw + `Stripe-Signature`.
3. Validar firma con `STRIPE_WEBHOOK_SECRET`.
4. Comprobar idempotencia por `stripeEventId`.
5. Procesar evento.
6. Actualizar estado interno (`Payment`, `Invoice`).
7. Registrar `StripeWebhookEvent` con resultado.
8. Responder 2xx si procesado o ya procesado.

### 3.5 Reembolsos
1. `POST /api/payments/{paymentId}/refund`.
2. Validar permisos, tenant y estado.
3. Crear refund en Stripe.
4. Marcar estado interno intermedio.
5. Confirmar estado final por webhook.
6. Reflejar impacto en `Payment` e `Invoice`.

### 3.6 Errores y pagos fallidos
- Manejar `payment_intent.payment_failed` con transición a `FAILED`.
- Mantener reintentos controlados.
- Alertar webhooks fallidos.
- Añadir reconciliación periódica backend↔Stripe.

---

## 4) Modelo de datos mínimo

### 4.1 Clinic
- `id`
- `name`
- `slug`
- `status`
- `createdAt`
- `updatedAt`

### 4.2 Patient
- `id`
- `clinicId`
- `name`
- `email`
- `phone`
- `createdAt`
- `updatedAt`

### 4.3 Invoice
- `id`
- `clinicId`
- `patientId`
- `amountMinor` (entero en céntimos)
- `currency`
- `status` (`DRAFT`, `PENDING_PAYMENT`, `PAYMENT_PROCESSING`, `PAID`, `PAYMENT_FAILED`, `REFUNDED`, `CANCELLED`)
- `description`
- `dueDate`
- `createdAt`
- `updatedAt`

### 4.4 Payment
- `id`
- `clinicId`
- `invoiceId`
- `patientId`
- `amountMinor`
- `currency`
- `status` (`CREATED`, `PROCESSING`, `SUCCEEDED`, `FAILED`, `REFUNDED`, `PARTIALLY_REFUNDED`, `DISPUTED`)
- `stripeCheckoutSessionId`
- `stripePaymentIntentId`
- `stripeChargeId`
- `stripeConnectedAccountId`
- `applicationFeeAmountMinor` (nullable)
- `paidAt`
- `failedAt`
- `refundedAt`
- `createdAt`
- `updatedAt`

### 4.5 StripeConnectedAccount
- `id`
- `clinicId` (unique)
- `stripeAccountId` (unique)
- `onboardingStatus`
- `chargesEnabled`
- `payoutsEnabled`
- `requirementsDue` (json/text)
- `capabilitiesSnapshot` (json/text)
- `lastSyncedAt`
- `createdAt`
- `updatedAt`

### 4.6 StripeWebhookEvent
- `id`
- `stripeEventId` (unique)
- `eventType`
- `stripeAccountId`
- `objectType`
- `objectId`
- `processingStatus` (`RECEIVED`, `PROCESSED`, `FAILED`, `IGNORED`)
- `attemptCount`
- `receivedAt`
- `processedAt`
- `errorMessage`
- `payloadHash` (opcional)

---

## 5) Endpoints backend (Spring Boot)

### Stripe Connect
- `POST /api/clinics/{clinicId}/stripe/account`
- `POST /api/clinics/{clinicId}/stripe/onboarding-link`
- `GET /api/clinics/{clinicId}/stripe/status`

### Pagos
- `POST /api/invoices/{invoiceId}/checkout-session`
- `GET /api/payments/{paymentId}`
- `POST /api/payments/{paymentId}/refund`

### Webhooks
- `POST /api/stripe/webhook`

### Servicios recomendados
- `StripeAccountService`
- `StripeOnboardingService`
- `StripeCheckoutService`
- `StripeWebhookService`
- `PaymentService`
- `InvoiceService`
- `RefundService`

---

## 6) Eventos webhook relevantes

Mínimos recomendados:
- `checkout.session.completed`
- `payment_intent.succeeded`
- `payment_intent.payment_failed`
- `charge.refunded`
- `charge.dispute.created`
- `account.updated` (estado de cuenta conectada)

> Confirmar eventos exactos según el flujo final y versión de API en documentación oficial:
- https://docs.stripe.com/connect
- https://docs.stripe.com/payments/checkout
- https://docs.stripe.com/webhooks

---

## 7) Variables de entorno

Mínimas:
- `STRIPE_SECRET_KEY`
- `STRIPE_PUBLISHABLE_KEY`
- `STRIPE_WEBHOOK_SECRET`
- `STRIPE_CONNECT_CLIENT_ID` (si aplica)
- `APP_BASE_URL`
- `FRONTEND_BASE_URL`
- `STRIPE_SUCCESS_URL`
- `STRIPE_CANCEL_URL`
- `APP_ENV` (`local`, `staging`, `production`)

Recomendadas:
- `STRIPE_API_VERSION`
- `STRIPE_MAX_NETWORK_RETRIES`
- `STRIPE_WEBHOOK_TOLERANCE_SECONDS`
- `PAYMENT_RECONCILIATION_CRON`
- `LOG_REDACTION_ENABLED`

---

## 8) Riesgos y decisiones pendientes

### Riesgos
- Mezcla accidental de tenants por validaciones insuficientes.
- Doble procesamiento de eventos sin idempotencia.
- Marcar factura pagada desde frontend.
- Mala segregación de claves por entorno.
- Falta de reconciliación ante pérdida/retraso de webhooks.

### Decisiones pendientes
- Tipo de cuenta conectada (`Express`, `Standard`, `Custom`) según negocio/compliance.
- Política de reembolsos (total/parcial, comisiones, reversión transferencias).
- Modelo de comisión futura (fija, porcentaje, suscripción, mixto).
- Reglas de reintento en pagos fallidos.

---

## 9) Plan de implementación por fases

### Fase 1 — MVP mínimo
- Connect onboarding por clínica.
- Checkout Session por factura.
- Webhook firmado + idempotencia.
- Actualización interna de `Payment` e `Invoice`.
- Sin comisiones.

### Fase 2 — Beta
- Estado de onboarding detallado.
- Refund parcial.
- Gestión más completa de fallos y reintentos.
- Dashboard interno de eventos y errores webhook.
- Job de reconciliación.

### Fase 3 — Producción
- Hardening seguridad/logs/redacción.
- Alertas operativas y observabilidad.
- Pruebas E2E con Stripe CLI (incluyendo eventos duplicados).
- Runbooks de soporte y operación.

### Fase 4 — Comisiones
- Activar comisión por transacción o suscripción mensual (Stripe Billing).
- Ajustar tratamiento contable/fiscal y refunds con comisión.
- Reportes por clínica y por plataforma.

---

## 10) Checklist final para producción

- [ ] Claves test/live separadas.
- [ ] Webhook live configurado y firma validada.
- [ ] HTTPS obligatorio.
- [ ] Idempotencia validada con duplicados.
- [ ] Frontend no confirma pagos.
- [ ] Validación tenant estricta en todos los endpoints.
- [ ] Reembolsos total/parcial probados.
- [ ] Onboarding y capacidades Connect probadas.
- [ ] Logs sin datos sensibles.
- [ ] Monitorización/alertas activas.
- [ ] Reconciliación automática y procedimiento manual definido.

---

## Datos sensibles que NO debes guardar nunca

- Número completo de tarjeta (PAN).
- CVC/CVV.
- Secret keys de Stripe.
- Tokens/secrets sensibles completos en logs.
- Payloads sensibles sin redacción.

---

## Entornos: local, staging, producción

### Local
- Claves de test.
- Stripe CLI para reenviar webhooks al backend local.
- Datos de prueba y tarjetas test.

### Staging
- Entorno casi idéntico a producción.
- Claves de test separadas de local.
- Pruebas E2E y de resiliencia (reintentos, duplicados, caídas parciales).

### Producción
- Claves live.
- Webhook live estable y monitorizado.
- Alertas, auditoría, runbooks y reconciliación operativa activas.
