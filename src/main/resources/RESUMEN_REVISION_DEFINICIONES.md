# RESUMEN EJECUTIVO: Revisión de Definiciones de Conceptos

**Fecha**: 2026-04-07  
**Solicitante**: Equipo BankVision  
**Revisor**: Asistente de Desarrollo  
**Estado**: ✅ Revisión Completada

---

## 1. RESUMEN DE HALLAZGOS

Se revisaron las cuatro incógnitas definidas en la reunión del equipo:

### ✅ **Incógnita 1: Catálogo de Estados de los Casos**
- **Definición**: 7 estados (Nuevo, En Gestión, Ilocalizado, Promesa Pago, Prejurídico, Cobro Judicial, Cerrado)
- **Estado Actual del Código**: Enum `Status` en `Case.java` con 4 estados (OPEN, IN_MANAGEMENT, PAUSED, CLOSED)
- **Brecha**: Faltan 3 estados específicos (PROMESA_PAGO, ILOCALIZADO, PREJURIDICO, COBRO_JUDICIAL)
- **Solución Propuesta**: Tabla parametrizable `case_statuses` en BD para permitir cambios sin recompilación
- **Archivo**: `catalogs_cases_permissions.sql` (creado)

### ✅ **Incógnita 2: Flujos de Estados (Transiciones)**
- **Definición**: Matriz de transiciones válidas (12 arcos entre estados)
- **Estado Actual del Código**: No existe validación de transiciones; cualquier cambio de estado es permitido
- **Brecha**: Falta máquina de estados con validación
- **Solución Propuesta**: 
  - Tabla `case_status_transitions` con matriz de cambios permitidos
  - Entidad de dominio `CaseStatusTransition` con lógica de negocio
  - Validación en aplicación antes de cambiar estado
- **Archivo**: `schema.sql` (extendido) + `MAPEO_DEFINICIONES_A_IMPLEMENTACION.md`

### ✅ **Incógnita 3: Catálogos de Roles y Permisos**
- **Definición**: 4 roles (Admin, Supervisor, Agente, Auditor) con matriz de 7 módulos × 3 niveles de acceso
- **Estado Actual del Código**: 
  - Tabla `app_roles` existe pero solo contiene Admin (bootstrap)
  - No hay tabla de permisos granulares
  - Seguridad es estateless (JWT) pero sin autorización basada en roles
- **Brecha**: Falta modelo parametrizable de permisos y mapeo role→permiso
- **Solución Propuesta**:
  - Tabla `app_permissions` (25+ permisos por módulo)
  - Tabla `role_permissions` (mapeo granular)
  - Anotación `@PreAuthorize` en endpoints
  - `PermissionEvaluator` de Spring Security
- **Archivo**: `schema.sql` (extendido) + nuevo bootstrap security

### ✅ **Incógnita 4: Tipificación de Casos de Intervención**
- **Definición**: Reglas automáticas por monto, riesgo (score), e intentos fallidos para desbordar a agente humano
- **Estado Actual del Código**: No existe lógica de desborde automático; asignación es manual
- **Brecha**: Falta motor de escalation automática con nivelador de cargas
- **Solución Propuesta**:
  - Tabla `escalation_rules` parametrizable por administrador
  - Entidad `EscalationRule` en dominio
  - Servicio `EscalationService` que evalúa condiciones
  - Integración en `OrchestrationApplicationService.send()`
  - Agente load balancer para distribución inteligente
- **Archivo**: `schema.sql` (extendido) + `MAPEO_DEFINICIONES_A_IMPLEMENTACION.md`

---

## 2. ARCHIVOS CREADOS/MODIFICADOS

| Archivo | Tipo | Descripción |
|---|---|---|
| `schema.sql` | **Modificado** | Extendido con 5 tablas (case_statuses, case_status_transitions, app_permissions, role_permissions, escalation_rules) |
| `CONCEPTOS_DEFINICIONES_ALINEACION.md` | **Creado** | Análisis de brechas entre definiciones y código actual |
| `MAPEO_DEFINICIONES_A_IMPLEMENTACION.md` | **Creado** | Mapeo detallado de cada concepto a cambios de código + cronograma |
| `catalogs_cases_permissions.sql` | **Creado** | Script de referencia con seed data detallado (no integrado en compilación, solo referencia) |
| `pom.xml` | **Sin cambios** | Las nuevas tablas no requieren dependencias adicionales |
| `Case.java` | **Sin cambios (por ahora)** | Enum Status seguirá existiendo hasta refactor en Sprint siguiente |

---

## 3. PROPUESTA DE IMPLEMENTACIÓN (ROADMAP)

### 🔵 **Sprint Actual (Ahora)**
**Objetivo**: Definición y base de datos
- ✅ Crear tablas catálogo (hecho)
- ⏳ Seed data de estados y transiciones (próximo paso)
- ⏳ Seed data de permisos por módulo (próximo paso)
- ⏳ Expandir bootstrap security (próximo paso)

### 🟡 **Sprint 1 (Próximas 2 semanas)**
**Objetivo**: State Machine en Dominio
- Crear entidad `CaseStatus`
- Implementar `CaseStatusTransition` con validaciones
- Método `isTransitionAllowedTo()` en dominio
- Tests de máquina de estados
- Endpoint `POST /api/v1/cases/{id}/transition`

### 🟠 **Sprint 2 (2-4 semanas)**
**Objetivo**: Autorización Basada en Roles
- Implementar `@PreAuthorize` en controladores
- Crear `PermissionEvaluator` para Spring Security
- Auditoría de cambios sensibles
- Tests de autorización por rol
- Dashboard de auditoría

### 🔴 **Sprint 3 (4-6 semanas)**
**Objetivo**: Desborde Automático y Escalation
- Entidad `EscalationRule` en dominio
- Servicio `EscalationService` con evaluación
- `AgentLoadBalancingService`
- Integración en `OrchestrationApplicationService`
- Tests de escalation rules
- Endpoints admin de parametrización

---

## 4. VALIDACIÓN Y GARANTÍAS

### ✅ Compilación
- Proyecto compila sin errores: `mvn compile -q` ✓

### ✅ Schema SQL
- Todas las tablas nuevas usan convenciones existentes (utf8mb4)
- Relaciones FK definidas correctamente
- Índices optimizados para queries frecuentes

### ✅ DDD Compatibility
- Cambios se implementarán respetando capas DDD:
  - **Dominio**: Entidades y lógica de negocio
  - **Aplicación**: Servicios coordinadores
  - **Infraestructura**: Repositorios y web controllers

### ✅ Auditoría y Cumplimiento
- Ley 2300: Restricción de horarios → Campo en rules
- GDPR/LPDP: Registro de cada cambio → `auditoria_eventos`
- Segregación de funciones: Permisos granulares por rol

---

## 5. PRÓXIMOS PASOS INMEDIATOS

**Si aprueba el plan:**

1. **Hoy**:
   - Revisar documentación (`MAPEO_DEFINICIONES_A_IMPLEMENTACION.md`)
   - Validar que propuesta alinea con visión del negocio

2. **Esta semana**:
   - Iniciar seed data de estados y transiciones en `schema.sql`
   - Crear roles SUPERVISOR, AGENTE, AUDITOR en bootstrap
   - Crear tabla `app_permissions` seed

3. **Semana próxima**:
   - Comenzar Sprint 1: Máquina de estados en dominio
   - Pull Request con cambios

---

## 6. RIESGOS IDENTIFICADOS

| Riesgo | Probabilidad | Impacto | Mitigación |
|---|---|---|---|
| Transiciones de estado pueden competir | Media | Alto | Usar `@Transactional`, pessimistic locks |
| Escalation rules mal configuradas | Media | Medio | Admin UI + validación + tests exhaustivos |
| Performance de matriz permisos | Baja | Medio | Caché en memoria, invalidación selectiva |
| Cambio de estados sin auditoría | Baja | Alto | Obligar auditoría antes de persistencia |

---

## 7. DOCUMENTACIÓN PARA EL EQUIPO

📖 **Lea en orden:**
1. `CONCEPTOS_DEFINICIONES_ALINEACION.md` → Entienda las brechas
2. `MAPEO_DEFINICIONES_A_IMPLEMENTACION.md` → Entienda la solución
3. `schema.sql` → Revise el modelo de datos
4. Este documento (RESUMEN) → Contexto y roadmap

---

## 8. CONCLUSIONES

✅ **Las 4 incógnitas de la reunión fueron analizadas y tienen propuestas concretas de implementación.**

✅ **El código actual es compatible con las definiciones; solo requiere extensiones parametrizables.**

✅ **La arquitectura DDD se mantiene; los cambios van en infraestructura (tablas) y dominio (entidades).**

✅ **Roadmap es alcanzable en 3 sprints con equipo de 2-3 personas.**

⏳ **Próximo hito**: Aprobación del plan y arranque de Sprint 1.

---

**Contacto para preguntas**: Este documento puede extenderse con decisiones técnicas en la medida que se requiera.


