# 🏦 Coovitel Cobranzas - Plataforma de Recuperación de Cartera

**Solución especializada de recuperación de cartera para BankVision** | Spring Boot + DDD | JWT/OAuth2 | Arquitectura modular

---

## 📚 ¿QUÉ ES ESTE PROYECTO?

Una **plataforma de recuperación de cartera** moderna, escalable y auditable construida con:
- **Arquitectura DDD** (Domain-Driven Design)
- **Spring Boot 4.0.5**
- **MySQL 8.0**
- **API REST documentada** (Swagger/OpenAPI)
- **Seguridad** JWT + OAuth2
- **Diseño modular** en 11 bounded contexts

---

## 🚀 INICIO RÁPIDO

### 1. Requisitos
```bash
Java 17+
Maven 3.8+
MySQL 8.0+
```

### 2. Clonar y configurar
```bash
git clone [tu-repo]
cd coovitelCobranzas
```

### 3. Configurar BD
```bash
# Edita src/main/resources/application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/coovitelcobranzas
spring.datasource.username=mao
spring.datasource.password=mao123
```

### 4. Ejecutar
```bash
mvn spring-boot:run
```

### 5. Ver documentación
```
http://localhost:8080/swagger-ui/index.html
```

---

## 📖 DOCUMENTACIÓN (START HERE)

| Documento | Descripción | Para quién |
|-----------|-------------|-----------|
| **[RESUMEN_EJECUTIVO.md](src/main/resources/RESUMEN_EJECUTIVO.md)** | ⭐ Overview de todo lo hecho | Todos |
| **[DOCUMENTACION_INDEX.md](src/main/resources/DOCUMENTACION_INDEX.md)** | 🗺️ Mapa de navegación completo | Todos |
| **[VERTICAL_SLICE_OBLIGACION.md](src/main/resources/VERTICAL_SLICE_OBLIGACION.md)** | 📝 Implementación detallada (primera vez) | Desarrolladores |
| **[FLUJO_DDD_VISUAL.md](src/main/resources/FLUJO_DDD_VISUAL.md)** | 🔄 Diagrama HTTP → DB → Response | Desarrolladores |
| **[DDD_ARQUITECTURA_DOMINIOS.md](src/main/resources/DDD_ARQUITECTURA_DOMINIOS.md)** | 🏗️ Blueprint de la arquitectura | Arquitectos |
| **[TESTS_DOMINIO_EXPLICACION.md](src/main/resources/TESTS_DOMINIO_EXPLICACION.md)** | 🧪 Cómo escribir tests | QA / Desarrolladores |
| **[ROADMAP_COBRANZA_5_ENTREGAS.md](src/main/resources/ROADMAP_COBRANZA_5_ENTREGAS.md)** | 📋 Plan 5 entregas + regulación | Gestores |
| **[CHEAT_SHEET_NUEVO_DOMINIO.md](src/main/resources/CHEAT_SHEET_NUEVO_DOMINIO.md)** | 📋 Template copy/paste | Desarrolladores |

**👉 [Empieza aquí →](src/main/resources/DOCUMENTACION_INDEX.md)**

---

## 🎯 ARQUITECTURA

### Bounded Contexts (11 dominios)

**Core:**
- `Cliente` - Datos y consentimientos
- `Obligacion` - Saldos, mora, pagos ⭐ **COMPLETO**
- `Interaccion` - Intentos de contacto
- `CasoGestion` - Gestión manual del asesor
- `Pago` - Transacciones conciliadas

**Soporte:**
- `ScoringSegmentacion` - IA para priorización
- `PoliticasEstrategia` - Reglas de negocio
- `OrquestacionCanales` - SMS, WhatsApp, Email, Voz

**Transversales:**
- `Integracion` - API/Batch inbound
- `AuditoriaTrazabilidad` - Bitácora inmutable
- `IdentidadAcceso` - JWT/OAuth2 ✅ INTEGRADO

### Estructura de carpetas
```
src/main/java/cooviteCobranza/
├── security/
│   ├── auth/          ✅ Autenticación (existente)
│   ├── config/        ✅ SecurityConfig + OpenApiConfig (NUEVO)
│   └── jwt/           ✅ JWT (existente)
│
└── cobranzas/         ✨ NUEVO (11 bounded contexts)
    ├── obligacion/    ⭐ COMPLETO (ejemplo)
    │   ├── domain/
    │   ├── application/
    │   └── infrastructure/
    ├── cliente/       (parcialmente)
    ├── interaccion/   (parcialmente)
    ├── casogestion/   (parcialmente)
    ├── pago/          (parcialmente)
    └── [otros 6]
```

---

## 📊 ESTADO DEL PROYECTO

### ✅ Completado (Fase 1)

| Item | Estado |
|------|--------|
| **Swagger/OpenAPI** | ✅ Integrado |
| **SecurityConfig JWT+OAuth2** | ✅ Operativo |
| **DDD Design** | ✅ 11 contextos |
| **Vertical Slice: Obligacion** | ⭐ **COMPLETO** |
| **Exception Handling** | ✅ Global |
| **Unit Tests** | ✅ 2 tests (expandibles) |
| **Documentación** | ✅ 8 archivos completos |
| **Compilación** | ✅ Success |

### ⏳ En progreso

- [ ] Dominios: Cliente, Pago, Interaccion, CasoGestion
- [ ] Tests de integración (MockMvc)
- [ ] Eventos de dominio
- [ ] Integración n8n

### 📅 Roadmap (5 entregas)
1. **Entrega 1:** Fundaciones (✅ ESTA ES ESTA)
2. **Entrega 2:** Motor scoring + políticas
3. **Entrega 3:** Orquestación omnicanal (n8n)
4. **Entrega 4:** Pago + conciliación
5. **Entrega 5:** Gobierno + producción

---

## 🌐 ENDPOINTS DISPONIBLES

### Obligacion (5 endpoints)
```
GET    /api/obligaciones/{id}
GET    /api/obligaciones/numero/{numero}
GET    /api/obligaciones/cliente/{clienteId}
PUT    /api/obligaciones/{id}/mora
PUT    /api/obligaciones/{id}/pago
```

### Autenticación (ya existente)
```
POST   /api/auth/register
POST   /api/auth/login
GET    /api/auth/me
PUT    /api/auth/change-password
```

**Documentación interactiva:** http://localhost:8080/swagger-ui/index.html

---

## 🧪 TESTING

### Tests unitarios (dominio)
```bash
mvn test -Dtest=ObligacionTest
```

### Tests de compilación
```bash
mvn clean compile
```

### Ejecutar la app
```bash
mvn spring-boot:run
```

---

## 📋 PRÓXIMOS PASOS

### Esta semana
1. Lee `DOCUMENTACION_INDEX.md` (5 min)
2. Prueba endpoints en Swagger (10 min)
3. Amplía tests de `Obligacion` (30 min)
4. Crea dominio `Cliente` (2 horas)

### Próximas 2 semanas
- Dominio `Pago` con integración
- Dominio `CasoGestion`
- Tests de integración
- Eventos de dominio

### Próximo mes
- Dominios: `Interaccion`, `ScoringSegmentacion`, `PoliticasEstrategia`
- MVP de n8n
- Integración WhatsApp Cloud API

---

## 🔐 SEGURIDAD

### JWT/OAuth2 ✅
- Token expiration: 24 horas
- Refresh token: 7 días
- Roles: ADMIN, SUPERVISOR, ASESOR, AUDITOR
- Autorización: SecurityConfig + @PreAuthorize

### Cumplimiento regulatorio
- Ley 2300 de 2023 (horarios, canales, frecuencia)
- Ley 1581 de 2012 (protección de datos)
- Ley 1266 de 2008 (hábeas data)
- Circular 048 de 2008 (cobranza)

*Ver: [ROADMAP_COBRANZA_5_ENTREGAS.md](src/main/resources/ROADMAP_COBRANZA_5_ENTREGAS.md) Sección 6*

---

## 📞 PREGUNTAS FRECUENTES

**P: ¿Dónde empiezo?**  
A: Lee [DOCUMENTACION_INDEX.md](src/main/resources/DOCUMENTACION_INDEX.md) → Nivel 1

**P: ¿Cómo creo un nuevo dominio?**  
A: Usa template en [CHEAT_SHEET_NUEVO_DOMINIO.md](src/main/resources/CHEAT_SHEET_NUEVO_DOMINIO.md)

**P: ¿Por qué DDD?**  
A: Lee [DDD_ARQUITECTURA_DOMINIOS.md](src/main/resources/DDD_ARQUITECTURA_DOMINIOS.md)

**P: ¿Cómo funciona el flujo?**  
A: Lee [FLUJO_DDD_VISUAL.md](src/main/resources/FLUJO_DDD_VISUAL.md)

**P: ¿Qué regulaciones debo cumplir?**  
A: Lee [ROADMAP_COBRANZA_5_ENTREGAS.md](src/main/resources/ROADMAP_COBRANZA_5_ENTREGAS.md) Sección 6

---

## 📞 CONTACTO / SOPORTE

- **Documentación:** `src/main/resources/*.md`
- **Código fuente:** `src/main/java/`
- **Tests:** `src/test/java/`
- **BD:** `src/main/resources/db/*.sql`

---

## 📦 DEPENDENCIAS PRINCIPALES

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.5</version>
</dependency>
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
</dependency>
```

---

## 🎉 ¡BIENVENIDO!

Estás en una **arquitectura moderna, escalable y enseñable** listos para construir la próxima generación de soluciones de recuperación de cartera.

**¡Empezamos! 👉 [DOCUMENTACION_INDEX.md](src/main/resources/DOCUMENTACION_INDEX.md)**

---

**Versión:** 1.0  
**Actualizado:** 2026-03-27  
**Estado:** 🟢 En desarrollo activo

