# MAPEO: Definiciones de Conceptos → Implementación en Código

## Documento de Referencia
Fecha: 2026-04-07  
Propósito: Establecer el vínculo entre definiciones de reunión y cambios de código

---

## 1. MATRIZ DE TRANSICIÓN DE ESTADOS (Casos)

### Definición:
| Estado Actual | Posibles Siguientes | Descripción |
|---|---|---|
| Nuevo | En Gestión, Ilocalizado | Caso entra al sistema |
| En Gestión | Promesa Pago, Ilocalizado, Prejurídico | Asesor/IA interactúan |
| Ilocalizado | En Gestión, Prejurídico | Canales agotados |
| Promesa Pago | Cerrado, En Gestión | Compromiso formalizado |
| Prejurídico | Cerrado, Cobro Judicial | Advertencia legal |
| Cobro Judicial | Cerrado | Proceso judicial |
| Cerrado | (fin) | Caso finalizado |

### Implementación:

#### Base de Datos
- **Tabla**: `case_statuses` (schema.sql)
  - Campos: `code`, `name`, `is_initial`, `is_final`, `enabled`
  - Seed: 7 estados según definición

- **Tabla**: `case_status_transitions` (schema.sql)
  - Campos: `from_status_id`, `to_status_id`, `trigger_event`
  - Relación: Muchos-a-muchos parametrizable
  - Seed: 12 transiciones según matriz

#### Dominio
- **Clase**: `CaseStatus` (nueva, en `casogestion/domain/model/`)
  - Reemplaza enum Status en `Case.java`
  - Valida transiciones permitidas
  - Método: `isTransitionAllowedTo(CaseStatus next)` → consulta `case_status_transitions`

- **Clase**: `CaseStatusTransition` (nueva, en `casogestion/domain/model/`)
  - Modela una transición válida
  - Contiene lógica de negocio de cambio de estado

#### Aplicación
- **Servicio**: Extender `CaseApplicationService`
  - Método: `transitionCase(Long caseId, CaseStatus targetStatus)` 
  - Valida permiso y transición
  - Lanza evento de dominio si es exitoso

- **Repositorio**: `CaseStatusRepository`
  - `findAllStatuses()` → listado de estados
  - `getTransitionsFrom(CaseStatus status)` → posibles siguientes

#### Controlador
- **Endpoint**: `POST /api/v1/cases/{id}/transition`
  - Parámetro: `targetStatus` (code)
  - Respuesta: caso con nuevo estado o error 409 (Conflict) si transición no permitida

---

## 2. CATÁLOGO DE ROLES Y PERMISOS

### Definición:
**Roles:**
- Admin, Supervisor, Agente, Auditor

**Matriz de Acceso** (7 módulos × 4 roles × 3 niveles: Full/Read/None)

### Implementación:

#### Base de Datos
- **Tabla**: `app_roles` (ya existe)
  - Seed: ADMIN, SUPERVISOR, AGENTE, AUDITOR (en bootstrap)

- **Tabla**: `app_permissions` (nueva, schema.sql)
  - 25+ permisos granulares (DASHBOARD:VIEW, INTEGRATION:UPLOAD, etc.)
  - Campos: `code`, `module`, `resource`, `action`

- **Tabla**: `role_permissions` (nueva, schema.sql)
  - Mapeo: rol ↔ permiso con `access_level` (FULL/READ/NONE)
  - Seed: 80+ filas según matriz de reunión

#### Dominio
- **Clase**: `Permission` (nueva, en `security/domain/model/`)
  - Identifica una operación granular del sistema
  - Agrupa por módulo y recurso

- **Clase**: `RolePermission` (nueva, en `security/domain/model/`)
  - Mapea un rol a permisos específicos
  - Validar acceso: `roleHasPermission(role, permission, accessLevel)`

#### Aplicación
- **Servicio**: `PermissionService`
  - `getRolePermissions(Role role)` → Set<Permission>
  - `canUserPerform(User user, String permissionCode)` → Boolean
  - `checkPermission(User user, String permissionCode)` → throw exception si no

#### Seguridad
- **Anotación**: `@PreAuthorize("hasPermission('CASE_MANAGEMENT:VIEW')")`
  - En controladores de negocio (CaseController, PaymentController, etc.)
  - En servicios de aplicación críticos

- **CustomPermissionEvaluator** (Spring Security)
  - Implementa `PermissionEvaluator`
  - Consulta `role_permissions` en tiempo de ejecución

---

## 3. DESBORDE AUTOMÁTICO A AGENTE HUMANO

### Definición:
El sistema debe permitir parametrizar reglas por:
- **Monto** (umbral económico)
- **Nivel de riesgo** (Score Crítico del cliente)
- **Intentos fallidos** (N contactos sin resultado)

Resultado: Asignación automática + nivelador de cargas

### Implementación:

#### Base de Datos
- **Tabla**: `escalation_rules` (nueva, schema.sql)
  - Campos: `rule_type` (AMOUNT/RISK/ATTEMPTS), `condition_field`, `condition_operator`, `condition_value`
  - Seed (ejemplos):
    - `AMOUNT`: Si `obligation.totalBalance > 10000000` → Desbordar
    - `RISK`: Si `scoring.segment == 'CRITICAL'` → Desbordar
    - `ATTEMPTS`: Si `interaccion.failedContactAttempts >= 3` → Desbordar

#### Dominio
- **Clase**: `EscalationRule` (nueva, en `orquestacion/domain/model/`)
  - Modelo de una regla parametrizable
  - Método: `evaluate(Obligation, ScoringResult, InteractionHistory)` → Boolean

- **Clase**: `EscalationEvaluation` (nueva, en `orquestacion/domain/model/`)
  - Resultado de evaluar todas las reglas
  - Contiene: `shouldEscalate`, `targetStatus`, `reason`

#### Aplicación
- **Servicio**: `EscalationService` (nuevo)
  - `evaluateForEscalation(caseId)` → EscalationEvaluation
  - `executeEscalation(caseId, evaluation)` → Case (ahora con asesor asignado)

- **Repositorio**: `EscalationRuleRepository`
  - `findEnabledRules()` → List<EscalationRule>

#### Integración en Orquestación
- **Extender**: `OrchestrationApplicationService.send()`
  ```
  After successful send():
    - Call evaluateForEscalation(caseId)
    - If shouldEscalate:
      - Call findAvailableAgent(skillSet) → Agent with lowest queue
      - Assign case to agent
      - Transition case to "En Gestión" or "Prejurídico" based on rule
      - Publish event "CaseEscalatedToAgent"
  ```

#### Controlador (Admin)
- **Endpoint**: `POST /api/v1/admin/escalation-rules`
  - Crear nueva regla parametrizable
- **Endpoint**: `PUT /api/v1/admin/escalation-rules/{id}`
  - Editar regla
- **Endpoint**: `GET /api/v1/admin/escalation-rules`
  - Listar reglas activas

---

## 4. RESPONSABLES HUMANOS Y ASIGNACIÓN MANUAL

### Definición:
- **Supervisor**: Actor principal para asignación manual
- **Administrador**: También puede asignar (pero es raro)
- **Motor IA**: Puede asignar automáticamente según reglas

### Implementación:

#### Permisos Requeridos
- `CASE_MANAGEMENT:ASSIGN` ← Asignación manual (Supervisor + Admin)
- Motor IA: Lógica interna sin permiso (sistema)

#### Endpoint de Asignación Manual
- **Endpoint**: `POST /api/v1/cases/{id}/assign-advisor`
  - Parámetro: `advisorId`, `reason`
  - Permiso requerido: `CASE_MANAGEMENT:ASSIGN` (Supervisor, Admin)
  - Auditoría: Registrar quién, cuándo, por qué

- **Endpoint**: `POST /api/v1/cases/batch-assign`
  - Asignación por lotes (load balancing)
  - Permiso: `CASE_MANAGEMENT:ASSIGN`

#### Nivelador de Cargas
- **Servicio**: `AgentLoadBalancingService`
  - `findBestAvailableAgent(Set<Agent>, skillSet)` → Agent
  - Usa: número de casos activos por agente
  - Prioriza: agentes con menos carga

---

## 5. TIPIFICACIÓN DE CASOS DE INTERVENCIÓN

### Definición:
Reglas automáticas según variables:
- Monto (threshold)
- Riesgo (score segment)
- Intentos (failed contacts)

**Resultado**: Tipificación automática del caso que requiere intervención humana

### Implementación:

#### Tipos de Intervención (Enum)
```
INTERVENCIÓN_POR_MONTO ("Deuda superior a $10M")
INTERVENCIÓN_POR_RIESGO ("Cliente en segmento CRITICAL")
INTERVENCIÓN_POR_INTENTOS ("3+ contactos sin respuesta")
INTERVENCIÓN_POR_AFINIDAD ("Cliente VIP o sensible")
```

#### Almacenamiento
- **Campo en `cases_gestion`**: `intervention_type` VARCHAR(50)
- **Campo en `escalation_rules`**: `intervention_classification` VARCHAR(100)

#### Lógica
- En `EscalationEvaluation`: incluir `interventionType`
- En auditoría: registrar tipificación aplicada
- En reporte: filtrar casos por tipo de intervención

---

## 6. AUDITORÍA Y CUMPLIMIENTO

### Definición:
"Auditoría inalterable de cada acción realizada en el sistema"

### Implementación:

#### Tabla Existente
- **Tabla**: `auditoria_eventos` (ya existe en schema.sql)
  - Registra: entidad, entidad_id, acción, usuario, detalle, timestamp

#### Eventos a Auditar (por permisos)
- ✅ Cambios de estado de caso (automáticos y manuales)
- ✅ Asignación de casos a asesores
- ✅ Cambios de políticas (POST/PUT)
- ✅ Creación de usuarios
- ✅ Cambio de contraseñas
- ✅ Accesos fallidos de login

#### Servicio de Auditoría
- **Servicio**: Extender `AuditApplicationService`
  - `recordCaseTransition(caseId, fromStatus, toStatus, user)`
  - `recordPermissionCheck(user, permissionCode, result)`
  - `recordSensitiveChange(entity, oldValue, newValue, user)`

---

## 7. CRONOGRAMA DE IMPLEMENTACIÓN

### Sprint Actual (Semana 1-2):
- [ ] Crear tablas catálogo en schema.sql ✅ (hecho)
- [ ] Seed data inicial de estados y transiciones
- [ ] Seed data de permisos por módulo
- [ ] Expandir bootstrap security con SUPERVISOR, AGENTE, AUDITOR + permisos

### Sprint Siguiente (Semana 3-4):
- [ ] Entidad `CaseStatus` en dominio
- [ ] Validación de transiciones
- [ ] Endpoint `POST /api/v1/cases/{id}/transition`
- [ ] Tests de state machine

### Sprint +1 (Semana 5-6):
- [ ] `@PreAuthorize` en controladores
- [ ] `PermissionEvaluator` de Spring Security
- [ ] Tests de autorización por rol

### Sprint +2 (Semana 7-8):
- [ ] `EscalationService` + `EscalationRule` en dominio
- [ ] Integración en `OrchestrationApplicationService`
- [ ] Endpoints admin de parametrización de reglas
- [ ] Tests de desborde automático

---

## 8. REFERENCIAS Y NOTAS

📋 **Documentos de referencia:**
- `CONCEPTOS_DEFINICIONES_ALINEACION.md` (este proyecto)
- Matriz de transiciones de reunión (Email de Camilo, 5 abril 2026)

🔗 **Clases involucradas:**
- Case → CaseStatus (renaming + expand)
- OrchestrationApplicationService → Add escalation logic
- AuthController → Extender con permiso management

⚠️ **Riesgos:**
- Transiciones de estado deben ser **atómicas** (usar @Transactional)
- Escalation rules deben evaluarse **sin delay** (ejecutar síncrono)
- Matriz de permisos puede crecer → considerar caché

✅ **Validación:**
- Cada transición debe tener test con casos válidos e inválidos
- Cada regla de desborde debe tener test con datos reales
- Auditoría debe registrar 100% de cambios sensibles


