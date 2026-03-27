# Diccionario de Traducciones - Español a Inglés

## Mapeo de Nombres de Clases y Métodos

### Palabras Clave Generales
- `Crear` → `Create`
- `Actualizar` → `Update`
- `Registrar` → `Register`
- `Obtener` → `Get`
- `Listar` → `List`
- `Eliminar` → `Delete`
- `Asignar` → `Assign`
- `Programar` → `Schedule`
- `Cerrar` → `Close`
- `Rechazar` → `Reject`
- `Confirmar` → `Confirm`
- `Cancelar` → `Cancel`
- `Activar` → `Activate`
- `Desactivar` → `Deactivate`

### Entidades de Negocio
- `CasoGestion` → `Case` / `CaseManagement`
- `Obligacion` → `Obligation`
- `Pago` → `Payment`
- `Estrategia` → `Strategy`
- `Politica` → `Policy`
- `Interaccion` → `Interaction`
- `Auditoria` → `Audit`
- `Scoring` → `Scoring` (mismo en inglés)
- `Segmentacion` → `Segmentation`
- `Asesor` → `Advisor`
- `Cliente` → `Client` / `Customer`
- `Mora` → `Delinquency`
- `Resultado` → `Result`

### Conceptos de Dominio
- `Id` → `Id` (igual)
- `Nombre` → `Name`
- `Descripcion` → `Description`
- `Estado` → `Status` / `State`
- `Prioridad` → `Priority`
- `Canal` → `Channel`
- `Plantilla` → `Template`
- `Rol` → `Role`
- `Referencia` → `Reference`
- `FechaCreacion` → `CreatedAt`
- `FechaActualizacion` → `UpdatedAt`
- `Intentos` → `Attempts`
- `Dias` → `Days`
- `DiasAntesDeeEscalacion` → `DaysBeforeEscalation`
- `MaxIntentosContacto` → `MaxContactAttempts`

### Sufijos Estándar (ya en inglés)
- `-Request` (se mantiene igual)
- `-Response` (se mantiene igual)
- `-Service` (se mantiene igual)
- `-Exception` (se mantiene igual)
- `-Repository` (se mantiene igual)
- `-Entity` (se mantiene igual)
- `-Event` (se mantiene igual)

## Archivos a Renombrar - Fase 1: DTOs

| Ruta Actual | Nuevo Nombre | Clase Interna | Nueva Clase |
|---|---|---|---|
| CrearInteraccionRequest.java | CreateInteractionRequest.java | CrearInteraccionRequest | CreateInteractionRequest |
| ActualizarResultadoInteraccionRequest.java | UpdateInteractionResultRequest.java | ActualizarResultadoInteraccionRequest | UpdateInteractionResultRequest |
| InteraccionResponse.java | InteractionResponse.java | InteraccionResponse | InteractionResponse |
| CrearCasoGestionRequest.java | CreateCaseRequest.java | CrearCasoGestionRequest | CreateCaseRequest |
| AsignarAsesorRequest.java | AssignAdvisorRequest.java | AsignarAsesorRequest | AssignAdvisorRequest |
| CasoGestionResponse.java | CaseResponse.java | CasoGestionResponse | CaseResponse |
| CrearEstrategiaRequest.java | CreateStrategyRequest.java | CrearEstrategiaRequest | CreateStrategyRequest |
| EstrategiaResponse.java | StrategyResponse.java | EstrategiaResponse | StrategyResponse |
| CrearPoliticaRequest.java | CreatePolicyRequest.java | CrearPoliticaRequest | CreatePolicyRequest |
| PoliticaResponse.java | PolicyResponse.java | PoliticaResponse | PolicyResponse |
| CrearPagoRequest.java | CreatePaymentRequest.java | CrearPagoRequest | CreatePaymentRequest |
| PagoResponse.java | PaymentResponse.java | PagoResponse | PaymentResponse |
| ConfirmarPagoRequest.java | ConfirmPaymentRequest.java | ConfirmarPagoRequest | ConfirmPaymentRequest |

## Archivos a Renombrar - Fase 2: Excepciones

| Ruta Actual | Nuevo Nombre | Clase Interna | Nueva Clase |
|---|---|---|---|
| InteraccionNotFoundException.java | InteractionNotFoundException.java | InteraccionNotFoundException | InteractionNotFoundException |
| InteraccionBusinessException.java | InteractionBusinessException.java | InteraccionBusinessException | InteractionBusinessException |
| CasoGestionNotFoundException.java | CaseNotFoundException.java | CasoGestionNotFoundException | CaseNotFoundException |
| CasoGestionBusinessException.java | CaseBusinessException.java | CasoGestionBusinessException | CaseBusinessException |
| EstrategiaNotFoundException.java | StrategyNotFoundException.java | EstrategiaNotFoundException | StrategyNotFoundException |
| PoliticasBusinessException.java | PolicyBusinessException.java | PoliticasBusinessException | PolicyBusinessException |
| PoliticaNotFoundException.java | PolicyNotFoundException.java | PoliticaNotFoundException | PolicyNotFoundException |
| PagoNotFoundException.java | PaymentNotFoundException.java | PagoNotFoundException | PaymentNotFoundException |
| PagoBusinessException.java | PaymentBusinessException.java | PagoBusinessException | PaymentBusinessException |

## Archivos a Renombrar - Fase 3: Servicios de Aplicación

| Ruta Actual | Nuevo Nombre | Clase Interna | Nueva Clase |
|---|---|---|---|
| InteraccionApplicationService.java | InteractionApplicationService.java | InteraccionApplicationService | InteractionApplicationService |
| CasoGestionApplicationService.java | CaseApplicationService.java | CasoGestionApplicationService | CaseApplicationService |
| PoliticaApplicationService.java | PolicyApplicationService.java | PoliticaApplicationService | PolicyApplicationService |
| EstrategiaApplicationService.java | StrategyApplicationService.java | EstrategiaApplicationService | StrategyApplicationService |
| PagoApplicationService.java | PaymentApplicationService.java | PagoApplicationService | PaymentApplicationService |
| ScoringSegmentacionApplicationService.java | ScoringSegmentationApplicationService.java | ScoringSegmentacionApplicationService | ScoringSegmentationApplicationService |

## Notas Importantes

1. **Orden de ejecución:** DTOs primero, luego excepciones, luego servicios, luego modelos, luego infraestructura.
2. **Compilación entre fases:** Compilar después de cada fase para validar.
3. **Tests:** Los tests también necesitarán actualización de imports después de cada cambio.
4. **Comentarios y JavaDoc:** Traducir en una fase separada después de los cambios de código.
5. **Base de datos:** Las columnas y tablas ya están en inglés, no requieren cambios.

