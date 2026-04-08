# ÍNDICE DE DOCUMENTACIÓN: Revisión de Definiciones de Conceptos

## 📚 Documentos Principales

### 1. **RESUMEN_REVISION_DEFINICIONES.md** ← **COMIENCE AQUÍ**
- **Propósito**: Resumen ejecutivo de la revisión
- **Contenido**: Hallazgos, roadmap, riesgos, conclusiones
- **Audiencia**: Gerencia, Product Owner, equipo completo
- **Tiempo de lectura**: 10 minutos

### 2. **CONCEPTOS_DEFINICIONES_ALINEACION.md**
- **Propósito**: Análisis detallado de brechas entre definición y código actual
- **Contenido**: 6 secciones (Estados, Roles, Desborde, Entidades, Timeline, Notas)
- **Audiencia**: Arquitectos, Líderes técnicos
- **Tiempo de lectura**: 15 minutos

### 3. **MAPEO_DEFINICIONES_A_IMPLEMENTACION.md** ← **PARA DESARROLLO**
- **Propósito**: Mapeo específico de cada concepto a piezas de código
- **Contenido**: Definición → BD → Dominio → Aplicación → Controlador para cada tema
- **Audiencia**: Desarrolladores que van a implementar
- **Tiempo de lectura**: 20 minutos + referencia continua durante codificación
- **Estructura**:
  - Sección 1: Matriz de Transición de Estados
  - Sección 2: Catálogo de Roles y Permisos
  - Sección 3: Desborde Automático
  - Sección 4: Asignación Manual y Load Balancing
  - Sección 5: Tipificación de Intervención
  - Sección 6: Auditoría y Cumplimiento
  - Sección 7: Cronograma Sprint por Sprint
  - Sección 8: Referencias y Notas

---

## 📊 Cambios en BD

### **schema.sql** (Modificado)
- Extendido con 5 nuevas tablas:
  - `case_statuses`: Catálogo parametrizable de estados (7 filas seed)
  - `case_status_transitions`: Matriz de transiciones (12 filas seed)
  - `app_permissions`: Catálogo de permisos (25+ filas seed)
  - `role_permissions`: Mapeo rol ↔ permiso (80+ filas seed)
  - `escalation_rules`: Reglas de desborde automático

### **catalogs_cases_permissions.sql** (Referencia solamente)
- Script de referencia con seed data detallado
- No está integrado en compilación
- Úselo como guía para entender las filas de seed esperadas

---

## 🔗 Cómo Usar Esta Documentación

### **Escenario 1: Soy Gerente/PO**
1. Lee: `RESUMEN_REVISION_DEFINICIONES.md` (sección 1-2, 7-8)
2. Aprueba: Roadmap en sección 3
3. Asigna: Sprints y recursos

### **Escenario 2: Soy Arquitecto/Tech Lead**
1. Lee: `CONCEPTOS_DEFINICIONES_ALINEACION.md` (completo)
2. Lee: `MAPEO_DEFINICIONES_A_IMPLEMENTACION.md` (secciones 1, 2, 8)
3. Revisa: Tablas nuevas en `schema.sql`
4. Define: Detalles técnicos faltantes (caché, seguridad, performance)

### **Escenario 3: Soy Desarrollador Backend**
1. Lee: `MAPEO_DEFINICIONES_A_IMPLEMENTACION.md` (sección relevante a tu tarea)
2. Consulta: Tabla de implementación (BD → Dominio → Aplicación → Controller)
3. Refiere: `schema.sql` para nombre/estructura de tablas
4. Implementa: Siguiendo estructura DDD propuesta
5. Testa: Según criterios en cada sección

### **Escenario 4: Soy Desarrollador Frontend / Integración**
1. Lee: `MAPEO_DEFINICIONES_A_IMPLEMENTACION.md` (solo secciones Controller/API)
2. Consulta: Endpoints propuestos (ej: `POST /api/v1/cases/{id}/transition`)
3. Espera: Swagger/OpenAPI del equipo backend

---

## ✅ Checklist de Seguimiento

### Antes de Iniciar Sprint
- [ ] Todos leyeron `RESUMEN_REVISION_DEFINICIONES.md`
- [ ] Tech Lead validó `MAPEO_DEFINICIONES_A_IMPLEMENTACION.md`
- [ ] DBA revisó `schema.sql` (extensiones nuevas)
- [ ] Se crearán las tablas antes de desarrollar código

### Durante Desarrollo (Sprint 1+)
- [ ] Cada commit referencia la sección de `MAPEO_...` aplicable
- [ ] Tests incluyen casos de la definición original
- [ ] Auditoría registra cambios sensibles
- [ ] Documentación de API (Swagger) alinea con definición

### Antes de Release
- [ ] Seed data de casos está cargado en BD
- [ ] Todos los roles (Admin, Supervisor, Agente, Auditor) tienen permisos asignados
- [ ] Prueba de transiciones de estado validada
- [ ] Prueba de escalation rules validada
- [ ] UAT con definiciones originales de reunión

---

## 📝 Notas Importantes

### Conceptos Clave Que Debe Recordar:

**Estado del Caso**: Cambió de enum simple a modelo parametrizable en BD. Permite cambios sin recompilación.

**Transiciones**: No todas son permitidas. Máquina de estados valida cada cambio.

**Permisos**: Granulares por módulo, recurso y acción. No es solo Admin/User, sino 25+ permisos específicos.

**Desborde Automático**: Reglas parametrizables (monto, riesgo, intentos) disparan asignación a agente con load balancing.

**Auditoría**: Cada cambio de estado y asignación DEBE registrarse. Ley 2300 lo requiere.

---

## 🔄 Historial de Cambios

| Fecha | Cambio | Archivos | Razón |
|---|---|---|---|
| 2026-04-07 | Creación inicial | 3 docs + schema.sql | Revisión de definiciones de reunión |

---

## 📞 Contacto y Preguntas

**¿Dudas sobre definiciones?** → Contacte al Equipo de Negocio (Camilo, Martha, Oscar)

**¿Dudas sobre implementación?** → Este documento + `MAPEO_DEFINICIONES_A_IMPLEMENTACION.md`

**¿Dudas técnicas de BD?** → Revise `schema.sql` y `catalogs_cases_permissions.sql`

**¿Cambios a la definición?** → Actualice `CONCEPTOS_DEFINICIONES_ALINEACION.md` y comunique al equipo

---

**Última actualización**: 2026-04-07  
**Estado**: ✅ Completo y listo para revisión


