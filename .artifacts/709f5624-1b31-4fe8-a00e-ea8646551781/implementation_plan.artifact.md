# Plan: Rediseño de Título "Planificador Semanal"

Este plan detalla el cambio del texto del título y la adición de un elemento gráfico curvo para igualar el estilo artístico de la referencia proporcionada, manteniendo la paleta de colores actual.

## User Review Required

> [!NOTE]
> El texto cambiará de "Menú Semanal" a "Planificador Semanal". Se agregará una línea ondulada decorativa que imita el trazo manual de la imagen de referencia.

## Proposed Changes

### [Utils]

#### [MODIFY] [pdfUtils.kt](file:///C:/Users/KAREN/AndroidStudioProjects/proyecto2/app/src/main/java/com/example/proyeto2/utils/pdfUtils.kt)

- **Actualización de `drawBohoHeader`:**
    - Cambiar la cadena de texto a `"Planificador Semanal"`.
    - Implementar un `Path` decorativo que dibuje una onda suave debajo del título.
    - Usar una línea muy fina con un color contrastante suave (posiblemente un tono dorado pálido o mantener el café suave para consistencia) para la curva.
    - Ajustar el margen horizontal para centrar mejor el texto más largo.

## Verification Plan

### Manual Verification
1. Generar el PDF.
2. Verificar que el título sea "Planificador Semanal".
3. Confirmar que aparezca la línea curva decorativa con el estilo "manuscrito".
