# AGENT.md

## Objetivo del agente

Este proyecto debe soportar internacionalización en la interfaz web, manteniendo el castellano como idioma principal y por defecto.

La aplicación debe estar disponible en:

* Castellano / Español (`es`)
* Euskera (`eu`)
* Inglés (`en`)

El idioma principal del producto es el castellano. Todas las nuevas funcionalidades, textos y pantallas deben diseñarse primero en castellano y después traducirse a euskera e inglés.

---

## Regla principal de idioma

El idioma por defecto de la aplicación es siempre:

```txt
es
```

No se debe usar inglés como idioma por defecto.

No se debe cambiar automáticamente el idioma inicial basándose únicamente en el idioma del navegador. Si no existe una preferencia previa del usuario, la aplicación debe arrancar en castellano.

Orden correcto de resolución del idioma:

1. Idioma guardado por el usuario, si existe y es válido.
2. Castellano (`es`) como fallback obligatorio.

No usar este orden:

1. Idioma del navegador.
2. Inglés.
3. Castellano.

---

## Idiomas soportados

Los únicos idiomas soportados inicialmente son:

```txt
es
eu
en
```

Cualquier otro valor debe considerarse inválido y resolverse a castellano.

Ejemplo:

```ts
const DEFAULT_LOCALE = "es";

const SUPPORTED_LOCALES = ["es", "eu", "en"] as const;
```

---

## Selector de idioma

La web debe incluir un selector de idioma visible para el usuario.

Opciones visibles recomendadas:

```txt
Castellano
Euskera
English
```

Valores internos:

```txt
es
eu
en
```

El selector debe:

* Mostrar el idioma actualmente activo.
* Permitir cambiar entre castellano, euskera e inglés.
* Guardar la preferencia del usuario.
* Aplicar el cambio en la UI sin romper el estado actual de la aplicación.

---

## Persistencia del idioma

La preferencia de idioma debe persistirse en el navegador.

Mecanismo recomendado:

```txt
localStorage
```

Clave recomendada:

```txt
app.locale
```

Comportamiento esperado:

* Si `localStorage["app.locale"]` existe y es `es`, `eu` o `en`, usar ese idioma.
* Si no existe, usar `es`.
* Si existe pero tiene un valor inválido, ignorarlo y usar `es`.

---

## Fallback de traducciones

El fallback obligatorio es castellano.

Si falta una traducción en euskera o inglés, la aplicación debe mostrar el texto castellano correspondiente.

Nunca debe mostrarse una clave técnica al usuario final salvo en entorno de desarrollo.

Ejemplo no deseado:

```txt
billing.payments.exportButton
```

Ejemplo correcto:

```txt
Exportar pagos
```

---

## Organización recomendada de traducciones

Agrupar las traducciones por dominio funcional.

Estructura recomendada:

```txt
src/
  i18n/
    index.ts
    locales/
      es.json
      eu.json
      en.json
```

O, si el proyecto ya tiene otra estructura, respetarla y adaptarse a ella.

Ejemplo de claves:

```json
{
  "common": {
    "save": "Guardar",
    "cancel": "Cancelar",
    "delete": "Eliminar",
    "edit": "Editar",
    "search": "Buscar",
    "loading": "Cargando..."
  },
  "navigation": {
    "patients": "Pacientes",
    "sessions": "Sesiones",
    "payments": "Pagos",
    "fees": "Tarifas",
    "exports": "Exportaciones"
  },
  "billing": {
    "title": "Facturación",
    "payments": {
      "title": "Pagos",
      "exportCsv": "Exportar pagos"
    },
    "pendingSessions": {
      "title": "Sesiones pendientes",
      "exportCsv": "Exportar sesiones pendientes"
    },
    "fees": {
      "title": "Tarifas",
      "exportCsv": "Exportar tarifas"
    },
    "summary": {
      "title": "Resumen de facturación",
      "exportCsv": "Exportar resumen"
    }
  }
}
```

---

## Textos hardcodeados

No deben quedar textos visibles hardcodeados en componentes.

Incorrecto:

```tsx
<button>Guardar</button>
```

Correcto:

```tsx
<button>{t("common.save")}</button>
```

También deben traducirse:

* Títulos.
* Botones.
* Labels.
* Placeholders.
* Mensajes de error.
* Mensajes de éxito.
* Validaciones.
* Modales.
* Confirmaciones.
* Estados vacíos.
* Cabeceras de tabla.
* Tooltips.
* Textos de navegación.
* Acciones de exportación.
* Textos relacionados con pacientes, sesiones, pagos, tarifas y facturación.

---

## Castellano como fuente principal

Cuando se añadan nuevos textos:

1. Añadir primero la clave en castellano.
2. Añadir después la traducción en euskera.
3. Añadir después la traducción en inglés.

El fichero `es` debe considerarse la referencia funcional.

Si una traducción no está clara, mantener la aplicación funcional y añadir un TODO claro en el fichero afectado.

Ejemplo:

```json
{
  "billing": {
    "summary": {
      "title": "TODO: revisar traducción"
    }
  }
}
```

---

## Reglas para nuevas pantallas

Toda nueva pantalla debe:

* Usar claves i18n desde el inicio.
* No introducir literales visibles directamente en JSX/HTML/templates.
* Usar castellano como fallback.
* Añadir las claves en los tres idiomas.
* Mantener nombres de clave consistentes con el resto del proyecto.

---

## Reglas para formularios

Los formularios deben traducir:

* Labels.
* Placeholders.
* Ayudas.
* Errores de validación.
* Mensajes de campo obligatorio.
* Mensajes de formato inválido.
* Botones de enviar, cancelar, limpiar o guardar.

Ejemplo de claves:

```json
{
  "patients": {
    "form": {
      "name": {
        "label": "Nombre",
        "placeholder": "Introduce el nombre"
      },
      "required": "Este campo es obligatorio"
    }
  }
}
```

---

## Reglas para errores

Los errores visibles para el usuario deben traducirse.

No mostrar directamente errores técnicos del backend salvo que ya estén normalizados para usuario final.

Preferir mensajes claros:

```txt
No se han podido cargar los pagos.
```

En lugar de:

```txt
Error 500: Internal Server Error
```

---

## Reglas para facturación y exportaciones

La aplicación contiene funcionalidades relacionadas con facturación, pagos, sesiones pendientes, tarifas y resúmenes.

Todos los textos de estas zonas deben estar internacionalizados:

* Exportar pagos.
* Exportar sesiones pendientes.
* Exportar tarifas.
* Exportar resumen de facturación.
* Fechas desde/hasta.
* Paciente.
* Estado activo/inactivo.
* Importes.
* Sesiones.
* Pagos.
* CSV.
* Mensajes de descarga.
* Mensajes de error al exportar.

El idioma de la interfaz no debe cambiar el contrato de los endpoints ni la lógica de negocio.

---

## No modificar backend salvo necesidad

La internacionalización afecta principalmente a la web/frontend.

No modificar:

* Endpoints.
* DTOs.
* Entidades.
* Repositorios.
* Servicios de negocio.
* Seguridad.
* Lógica de facturación.

Solo modificar backend si existe una razón técnica imprescindible y documentarla claramente.

---

## Calidad esperada

Antes de terminar cualquier tarea de i18n, comprobar:

* La aplicación compila.
* No hay imports muertos.
* No hay claves duplicadas innecesarias.
* No quedan textos principales hardcodeados.
* El selector de idioma funciona.
* El idioma se guarda correctamente.
* El fallback a castellano funciona.
* El idioma por defecto es castellano.
* Los tests existentes siguen pasando, si existen.

---

## Criterios de aceptación

Una tarea de internacionalización se considera terminada cuando:

* La web abre en castellano por defecto.
* Existe selector de idioma.
* Se puede cambiar a euskera.
* Se puede cambiar a inglés.
* El idioma elegido se mantiene al recargar.
* Si se elimina la preferencia, vuelve a castellano.
* Los textos visibles principales están traducidos.
* El fallback a castellano está configurado.
* No se ha alterado la lógica de negocio.
