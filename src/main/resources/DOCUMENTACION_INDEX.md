# 📚 Documentación del Proyecto - Índice

Este es el **centro de documentación** del proyecto de recuperación de cartera. Cada archivo explica un aspecto diferente de la arquitectura y el aprendizaje.

---

## 🎯 Documentos principales

### 1. **ROADMAP_COBRANZA_5_ENTREGAS.md**
- **Qué es:** Plan estratégico del proyecto completo en 5 entregas
- **Para quién:** Gestores de proyecto, stakeholders, desarrolladores que necesitan contexto
- **Incluye:**
  - Objetivos de negocio
  - Dominios DDD identificados
  - Decisión de IA (XGBoost + SHAP)
  - Marco legal colombiano aplicable
  - Ruta de aprendizaje recomendada (n8n, WhatsApp, IA)

**🔗 Empezar aquí si:** Necesitas entender qué construiremos y por qué.

---

### 2. **DDD_ARQUITECTURA_DOMINIOS.md**
- **Qué es:** Blueprint de la arquitectura DDD del proyecto
- **Para quién:** Arquitectos, desarrolladores senior, revisores
- **Incluye:**
  - 11 bounded contexts definidos (core, soporte, transversales)
  - Context map con relaciones entre dominios
  - Estructura de capas (domain, application, infrastructure, web)
  - Agregados raíz por contexto
  - Reglas de acoplamiento entre contextos
  - Eventos de negocio recomendados
  - Orden de construcción incremental

**🔗 Empezar aquí si:** Necesitas entender cómo está dividida la arquitectura.

---

### 3. **VERTICAL_SLICE_OBLIGACION.md** ⭐ START HERE
- **Qué es:** Guía completa de un vertical slice real (dominio `Obligacion`)
- **Para quién:** Desarrolladores que recién se integran, principiantes en DDD
- **Incluye:**
  - Flujo HTTP → Dominio → BD (diagrama paso a paso)
  - Estructura de carpetas creada
  - Endpoints REST disponibles (5 endpoints)
  - Ejemplos de request/response exitosos y con error
  - Reglas de negocio implementadas
  - Capa de persistencia (JPA → Domain)
  - Tests unitarios
  - Próximos pasos recomendados

**🔗 Empezar aquí si:** Acabas de clonar el repo y quieres ver qué se hizo.

---

### 4. **TESTS_DOMINIO_EXPLICACION.md**
- **Qué es:** Tutorial de tests unitarios del dominio (por qué, cómo, qué agregar)
- **Para quién:** QA, desarrolladores nuevos en testing, TDD enthusiasts
- **Incluye:**
  - Explicación línea por línea de los 2 tests de `ObligacionTest`
  - Qué regla de negocio valida cada test
  - 3 tests adicionales sugeridos (copiar/pegar)
  - Cómo ejecutar tests
  - Pirámide de testing (unit → integration → E2E)
  - Checklist: test bien escrito
  - Cómo los tests protegen contra refactorings accidentales

**🔗 Empezar aquí si:** No entiendes para qué sirven los tests o quieres escribir más.

---

### 5. **FLUJO_DDD_VISUAL.md**
- **Qué es:** Diagrama detallado de cómo fluye una solicitud por todas las capas
- **Para quién:** Desarrolladores visuales, personas que necesitan entender "el big picture"
- **Incluye:**
  - Flujo HTTP completo (cliente → API → DB → respuesta)
  - Cada capa con código ejemplo
  - Conversiones (Domain ↔ JPA Entity)
  - Manejo de excepciones por capa
  - Mapa de responsabilidades por archivo
  - Por qué esta arquitectura resuelve problemas comunes

**🔗 Empezar aquí si:** Necesitas "ver" cómo fluye el código de punta a punta.

---

## 📖 Cómo leer la documentación

### Si eres **NUEVO en el proyecto:**

1. Abre `ROADMAP_COBRANZA_5_ENTREGAS.md` (contexto de negocio)
2. Lee `DDD_ARQUITECTURA_DOMINIOS.md` (cómo está dividido)
3. Abre `VERTICAL_SLICE_OBLIGACION.md` (qué ya está implementado)
4. Prueba los endpoints (abajo)
5. Lee `FLUJO_DDD_VISUAL.md` (entiende el flujo)

### Si eres **DESARROLLADOR** asignado a nuevos dominios:

1. Lee `DDD_ARQUITECTURA_DOMINIOS.md` (tu dominio)
2. Copia la estructura de `obligacion/` a tu nuevo dominio
3. Adapta `ObligacionController`, `ObligacionApplicationService`, etc.
4. Sigue patrón en `VERTICAL_SLICE_OBLIGACION.md`

### Si eres **QA/Tester:**

1. Lee `VERTICAL_SLICE_OBLIGACION.md` (endpoints)
2. Prueba los 5 endpoints (abajo)
3. Lee `TESTS_DOMINIO_EXPLICACION.md` (casos de prueba)
4. Escribe tests de integración

### Si eres **DevOps/Infra:**

1. Lee sección "Configuración necesaria" en `VERTICAL_SLICE_OBLIGACION.md`
2. Revisa `ROADMAP_COBRANZA_5_ENTREGAS.md` sección 3 (legal/cumplimiento)
3. Prepara ambientes (dev, test, prod) con BD MySQL

---

## 🚀 Probar ahora mismo

### Paso 1: Asegúrate que BD está lista
```bash
mysql -u mao -p'mao123' -e "USE coovitelcobranzas; SHOW TABLES;"
```

### Paso 2: Compila y ejecuta
```bash
cd /home/fvillanueva/Escritorio/coovitelCobranzas
mvn spring-boot:run
```

### Paso 3: Abre Swagger
```
http://localhost:8080/swagger-ui/index.html
```
Verás bajo tag **"Obligaciones"** los 5 endpoints.

### Paso 4: Prueba GET (consultar)
```bash
curl -X GET http://localhost:8080/api/obligaciones/1
```

Si la BD ya tiene datos, verás la obligación.

### Paso 5: Prueba PUT (aplicar pago)
```bash
curl -X PUT http://localhost:8080/api/obligaciones/1/pago \
  -H "Content-Type: application/json" \
  -d '{"valorPago": 50000}'
```

Deberías ver el saldo reducido en la respuesta.

---

## 📁 Estructura de archivos de documentación

```
src/main/resources/
├── ROADMAP_COBRANZA_5_ENTREGAS.md         ← Plan del proyecto completo
├── DDD_ARQUITECTURA_DOMINIOS.md           ← Blueprint de arquitectura
├── VERTICAL_SLICE_OBLIGACION.md           ⭐ START HERE
├── TESTS_DOMINIO_EXPLICACION.md           ← Cómo testear dominio
├── FLUJO_DDD_VISUAL.md                    ← Diagrama detallado
├── DOCUMENTACION_INDEX.md                 ← Este archivo
├── application.properties
└── db/
    ├── schema.sql                         ← Estructura BD
    ├── auth_schema.sql
    └── [otros scripts]
```

---

## 🔍 Mapa rápido: ¿Dónde está qué?

| Necesito entender... | Leer... |
|---|---|
| **Qué es DDD y por qué lo usamos** | DDD_ARQUITECTURA_DOMINIOS.md |
| **Cómo funciona un endpoint** | FLUJO_DDD_VISUAL.md |
| **Qué endpoints están disponibles** | VERTICAL_SLICE_OBLIGACION.md (sección "Endpoints") |
| **Cómo escribir tests** | TESTS_DOMINIO_EXPLICACION.md |
| **Regulaciones colombianas que afectan** | ROADMAP_COBRANZA_5_ENTREGAS.md (sección 6) |
| **Decisión de IA para scoring** | ROADMAP_COBRANZAS_5_ENTREGAS.md (sección 2) |
| **Cómo replicar este patrón en otro dominio** | VERTICAL_SLICE_OBLIGACION.md |
| **Plan completo (5 entregas)** | ROADMAP_COBRANZAS_5_ENTREGAS.md |
| **Qué dominios existen y cómo se relacionan** | DDD_ARQUITECTURA_DOMINIOS.md (Context Map) |

---

## ✅ Checklist de lectura recomendada

- [ ] Leo `ROADMAP_COBRANZA_5_ENTREGAS.md` (contexto)
- [ ] Leo `DDD_ARQUITECTURA_DOMINIOS.md` (architecture)
- [ ] Pruebo los 5 endpoints en Swagger UI (hands-on)
- [ ] Leo `VERTICAL_SLICE_OBLIGACION.md` (implementation)
- [ ] Ejecuto tests: `mvn test -Dtest=ObligacionTest`
- [ ] Leo `TESTS_DOMINIO_EXPLICACION.md` (understanding tests)
- [ ] Leo `FLUJO_DDD_VISUAL.md` (deep dive)
- [ ] Hago cambios en `Obligacion.java` y veo fallar los tests
- [ ] Escribo un nuevo test (por ejemplo, Test 3 sugerido)
- [ ] Estoy listo para crear un nuevo dominio

---

## 🎓 Aprendizaje progresivo

### Nivel 1: Conceptos (30 min)
- [ ] ROADMAP (sección 3: dominios DDD)
- [ ] DDD_ARQUITECTURA (context map)

### Nivel 2: Implementación (1 hora)
- [ ] VERTICAL_SLICE_OBLIGACION (estructura)
- [ ] Probar endpoints en Swagger

### Nivel 3: Pruebas (30 min)
- [ ] TESTS_DOMINIO_EXPLICACION (tests existentes)
- [ ] Ejecutar tests unitarios
- [ ] Escribir Test 3

### Nivel 4: Flujo completo (1 hora)
- [ ] FLUJO_DDD_VISUAL (HTTP → DB)
- [ ] Trazar una solicitud paso a paso
- [ ] Entender conversiones (Domain ↔ JPA Entity)

### Nivel 5: Crear nuevo dominio (2-3 horas)
- [ ] Copiar estructura de `obligacion/`
- [ ] Adaptarla a tu nuevo dominio
- [ ] Crear tests
- [ ] Crear endpoints

---

## 💡 Términos clave (glosario rápido)

| Término | Significado |
|---------|------------|
| **DDD** | Domain-Driven Design. Arquitectura donde el dominio (lógica de negocio) es el centro. |
| **Bounded Context** | Límite claro de un dominio. Ej: "Obligacion", "Cliente", "Pago". |
| **Aggregate Root** | Entidad principal de un dominio que asegura invariantes (reglas). Ej: `Obligacion`. |
| **Repository** | Interface que abstrae persistencia. Dominio NO sabe si usa BD o archivo. |
| **Application Service** | Orquestador de casos de uso. Traduce entre HTTP y dominio. |
| **DTO** | Data Transfer Object. JSON para cliente (no es dominio). |
| **Entity** | En DDD = tiene identidad única. En JPA = tabla con @Entity. |
| **Value Object** | Objeto sin identidad, inmutable. Ej: estado (AL_DIA, EN_MORA). |
| **Event** | Evento de negocio. Ej: `PagoConfirmado` notifica a otros dominios. |

---

## 📞 Preguntas frecuentes (FAQ)

**P: ¿Por qué `Obligacion` no tiene anotaciones JPA?**  
R: Para mantener el dominio puro e independiente de Spring/JPA. Las anotaciones van en `ObligacionJpaEntity` (persistencia).

**P: ¿Cómo agrego un nuevo campo a `Obligacion`?**  
R: (1) Agrega a `Obligacion.java`, (2) Agrega a `ObligacionJpaEntity.java`, (3) Agrega a tabla `obligation` (SQL), (4) Actualiza DTOs, (5) Escribe test.

**P: ¿Qué diferencia hay entre `ObligacionNotFoundException` y `ObligacionBusinessException`?**  
R: `NotFound` = recurso no existe (404). `BusinessException` = lógica rechazó operación (400).

**P: ¿Los tests unitarios deben testear la persistencia?**  
R: No. Tests unitarios = dominio puro. Tests de integración = dominio + persistencia.

**P: ¿Por qué `@Transactional` en Application Service y no en Controller?**  
R: Transacción ≥ caso de uso lógico. Varias operaciones DB deben estar juntas o nada.

---

## 🚦 Próximos pasos recomendados

1. **Corto plazo (esta semana):**
   - [ ] Entiende el vertical slice actual (`VERTICAL_SLICE_OBLIGACION.md`)
   - [ ] Amplía tests de `Obligacion` (Test 3, 4, 5 sugeridos)
   - [ ] Crea dominio `Cliente` siguiendo el mismo patrón

2. **Mediano plazo (próximas 2 semanas):**
   - [ ] Dominio `Pago` (integración con `Obligacion`)
   - [ ] Dominio `CasoGestion`
   - [ ] Tests de integración (MockMvc)
   - [ ] Eventos de dominio (DomainEvent)

3. **Largo plazo (mes 1):**
   - [ ] Dominio `Interaccion`
   - [ ] Dominio `ScoringSegmentacion`
   - [ ] Dominio `PoliticasEstrategia`
   - [ ] Integración con n8n para orquestación

---

**Última actualización:** 2026-03-27  
**Versión:** 1.0  
**Mantenedor:** [Tu nombre]


