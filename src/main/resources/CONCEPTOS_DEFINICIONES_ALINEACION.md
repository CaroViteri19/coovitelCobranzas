# Análisis y Alineación: Definiciones de Proyecto vs. Código Actual

## Documento de Trabajo
Fecha: 2026-04-07  
Referencia: Definiciones de concepto de casos, roles y permisos

---

## 1. MATRIZ DE TRANSICIÓN DE ESTADOS DE CASOS

### Definición de la Reunión
| Estado Actual | Predecesor(es) | Estado Siguiente | Descripción |
|---|---|---|---|
| **Nuevo** | Importación CORE / Batch | En Gestión, Ilocalizado/Sin respuesta | El caso entra al sistema y espera la primera acción |
| **En Gestión** | Nuevo, Ilocalizado, Promesa Incumplida | Promesa Pago, Ilocalizado, Prejurídico | El asesor o la IA están interactuando |
| **Ilocalizado/Sin respuesta** | Nuevo, En Gestión | En Gestión, Prejurídico | Se han agotado los canales sin éxito |
| **Promesa Pago** | En Gestión | Cerrado, En Gestión | Se formaliza un compromiso |
| **Prejurídico** | En Gestión, Ilocalizado | Cerrado, Cobro Judicial | Etapa de advertencia legal |
| **Cerrado** | Promesa Pago, Prejurídico | (Fin del ciclo) | Deuda normalizada o castigada |

### Estado Actual del Código
`Case.java` define enum Status:
- `OPEN` (equivalente a "Nuevo")
- `IN_MANAGEMENT` (equivalente a "En Gestión")
- `PAUSED` (parcialmente "Ilocalizado/Sin respuesta")
- `CLOSED` (equivalente a "Cerrado")

**Brecha**: Faltan estados `PAYMENT_PROMISE`, `UNLOCALIZED`, `PREJUDICIAL`, `JUDICIAL_COLLECTION`

---

## 2. CATÁLOGO DE ROLES Y PERMISOS

### Definición de la Reunión
**Roles:**
- **Admin**: Control total, gestión de usuarios y parámetros técnicos
- **Supervisor**: Monitoreo de KPIs, gestión de estrategias, supervisión de agentes
- **Agente**: Ejecución operativa, contacto y acuerdos
- **Auditor**: Verificación de procesos, logs y cumplimiento

### Estado Actual del Código
`app_roles` table contiene solo:
- `ADMIN` (semilla bootstrap)

**Brecha**: Falta modelo parametrizable de roles, permisos por módulo y matriz de acceso

---

## 3. REGLAS DE DESBORDE AUTOMÁTICO

### Definición de la Reunión
El sistema debe permitir parametrizar reglas por:
- **Monto** (umbral económico)
- **Nivel de riesgo** (Score crítico)
- **Intentos fallidos** (N contactos sin resultado)

Resultado: Asignación automática a agente humano con nivelador de cargas

### Estado Actual del Código
`OrchestrationApplicationService` tiene:
- `send()`: Envía mensajes
- `getById()`: Recupera ejecución
- `listByCase()`: Lista por caso

**Brecha**: No hay lógica de desborde automático, ni reglas de riesgo o intentos

---

## 4. PROPUESTA DE IMPLEMENTACIÓN

### Fase 1: Catálogos de Base
1. Crear tabla `case_statuses` (parametrizable)
2. Crear tabla `case_status_transitions` (matriz de transiciones)
3. Crear tabla `app_permissions` (permisos en el sistema)
4. Crear tabla `role_permissions` (mapeo rol ↔ permiso)

### Fase 2: Entidades de Dominio
1. Enum mejorado en `Case.java` o modelo de entidad
2. Entidad `CaseStatus` con transiciones validadas
3. Entidad `Permission` con cobertura de módulos

### Fase 3: Lógica de Desborde Automático
1. Crear reglas parametrizables en tabla `escalation_rules`
2. Extender `OrchestrationApplicationService.send()` con lógica de desborde
3. Crear servicio `EscalationRuleService` para evaluación

### Fase 4: Autorización en Endpoints
1. Integrar `@PreAuthorize` en controladores
2. Mapear permisos a operaciones (CRUD + operaciones específicas)
3. Auditar cambios sensibles

---

## 5. TIMELINE PROPUESTO

**Sprint Actual:**
- [ ] Crear catálogos SQL para estados y transiciones
- [ ] Crear tablas de permisos y roles
- [ ] Seed data con roles y permisos base

**Sprint Siguiente:**
- [ ] Implementar validación de transiciones en `Case`
- [ ] Integrar autorización con `@PreAuthorize`
- [ ] Crear UI/API para admin de roles y permisos

**Sprint Futuro:**
- [ ] Reglas parametrizables de desborde automático
- [ ] Nivelador de cargas para asignación automática
- [ ] Dashboard de supervisión

---

## 6. NOTAS IMPORTANTES

✅ **Fortalezas actuales:**
- Estructura DDD clara
- Modelo de entidades de usuario y rol ya existe
- Bootstrap de admin funcional

⚠️ **Riesgos:**
- Estado del caso es actualmente un enum simple (sin transiciones validadas)
- No hay restricción de transiciones inválidas
- Autorización por roles no implementada aún
- Desborde automático requiere integración compleja

📋 **Próximos pasos recomendados:**
1. Validar esta propuesta con el equipo de negocio
2. Iniciar con catálogos y permiso en Sprint actual
3. Pruebas de transiciones de estado en Sprint siguiente

