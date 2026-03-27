# Quick Start Guide - DDD Modules Implementation

**Última actualización**: 27 de Marzo de 2026  
**Estado**: ✅ COMPILACIÓN EXITOSA (94 archivos)

---

## ⚡ 5 Minutos para Empezar

### 1. Verificar Compilación ✅
```bash
cd /home/fvillanueva/Escritorio/coovitelCobranzas
./mvnw clean compile
# Output esperado: BUILD SUCCESS
```

### 2. Crear Base de Datos
```bash
# En tu cliente MySQL/MariaDB:
mysql -u root -p

# Ejecutar scripts en orden:
source /ruta/a/schema.sql
source /ruta/a/schema_ddd_modules.sql
source /ruta/a/auth_schema.sql
```

### 3. Iniciar Aplicación
```bash
./mvnw spring-boot:run
# Esperado: Tomcat started on port 8080
```

### 4. Probar Primer Endpoint
```bash
# Crear un cliente
curl -X POST http://localhost:8080/api/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "tipoDocumento": "CC",
    "numeroDocumento": "123456",
    "nombreCompleto": "Test User"
  }'

# Respuesta esperada (201 CREATED):
# { "id": 1, "tipoDocumento": "CC", ... }
```

---

## 📋 Módulos Disponibles

| Módulo | Base URL | Métodos |
|--------|----------|---------|
| **Cliente** | `/api/clientes` | 5 endpoints |
| **Pago** | `/api/pagos` | 6 endpoints |
| **Interacción** | `/api/interacciones` | 4 endpoints |
| **CasoGestion** | `/api/casos-gestion` | 6 endpoints |

---

## 🧪 Ejemplos Rápidos

### Cliente
```bash
# POST - Crear
curl -X POST http://localhost:8080/api/clientes \
  -H "Content-Type: application/json" \
  -d '{"tipoDocumento":"CC","numeroDocumento":"123","nombreCompleto":"Juan"}'

# GET - Obtener por ID
curl http://localhost:8080/api/clientes/1

# PUT - Actualizar contacto
curl -X PUT http://localhost:8080/api/clientes/1/contacto \
  -H "Content-Type: application/json" \
  -d '{"telefono":"3001234567","email":"juan@test.com"}'
```

### Pago
```bash
# POST - Crear
curl -X POST http://localhost:8080/api/pagos \
  -H "Content-Type: application/json" \
  -d '{"obligacionId":1,"valor":500,"referenciaExterna":"PAG001","metodo":"PSE"}'

# POST - Confirmar
curl -X POST http://localhost:8080/api/pagos/confirmar \
  -H "Content-Type: application/json" \
  -d '{"referencia":"PAG001"}'
```

### CasoGestion
```bash
# POST - Crear
curl -X POST http://localhost:8080/api/casos-gestion \
  -H "Content-Type: application/json" \
  -d '{"obligacionId":1,"prioridad":"ALTA"}'

# GET - Listar pendientes
curl http://localhost:8080/api/casos-gestion/pendientes

# PUT - Asignar asesor
curl -X PUT http://localhost:8080/api/casos-gestion/1/asesor \
  -H "Content-Type: application/json" \
  -d '{"asesor":"Maria González"}'
```

---

## 📚 Documentación Completa

Para guías detalladas, ver:

| Documento | Contenido |
|-----------|----------|
| `TESTING_GUIDE.md` | 50+ ejemplos curl, flujos e2e, casos de error |
| `IMPLEMENTACION_DDD_MODULOS_CORE_V2.md` | Detalles técnicos, patrones, arquitectura |
| `RESUMEN_EJECUTIVO_IMPLEMENTACION_V2.md` | Overview ejecutivo, roadmap, decisiones |
| `INVENTORY_ARCHIVOS_CREADOS.md` | Inventario completo de 68 archivos |

---

## 🛠️ Troubleshooting

### Compilación falla
```bash
# Limpiar y recompilar
./mvnw clean install
# Si aún falla, verificar Java version (debe ser 17+)
java -version
```

### BD no conecta
```bash
# Verificar credenciales en application.properties
# Por defecto: root/root en localhost:3306
# Tabla: coovitel_db

# Crear BD si no existe:
mysql -u root -p
CREATE DATABASE coovitel_db;
USE coovitel_db;
source schema.sql
source schema_ddd_modules.sql
```

### Endpoint retorna 404
```bash
# Verificar:
# 1. URL correcta (sin typos)
# 2. Aplicación está corriendo (http://localhost:8080)
# 3. Datos en BD (GET antes de POST)
```

---

## 🎯 Flujo Típico

```
1. POST /api/clientes          (Crear cliente)
   ↓
2. POST /api/obligaciones      (Crear obligación - módulo existente)
   ↓
3. POST /api/casos-gestion     (Crear caso)
   ↓
4. PUT  /api/casos-gestion/1/asesor  (Asignar asesor)
   ↓
5. POST /api/interacciones     (Registrar contacto)
   ↓
6. PUT  /api/interacciones/1/resultado  (Marcar entregada)
   ↓
7. POST /api/pagos             (Registrar pago)
   ↓
8. POST /api/pagos/confirmar   (Confirmar pago)
   ↓
9. PUT  /api/casos-gestion/1/cerrar  (Cerrar caso)
```

---

## 🔐 Respuestas de Error Comunes

### 404 Not Found
```json
{
  "timestamp": "2026-03-27T10:44:07",
  "status": 404,
  "error": "Cliente no encontrado",
  "message": "Cliente no encontrado con ID: 999"
}
```

### 400 Bad Request
```json
{
  "timestamp": "2026-03-27T10:44:07",
  "status": 400,
  "error": "Error de negocio",
  "message": "Pago ya existe con referencia: PAG-001"
}
```

---

## 📊 Estado Actual

| Componente | Status |
|-----------|--------|
| Compilación | ✅ SUCCESS (94 files) |
| Cliente Module | ✅ COMPLETO (15 archivos) |
| Pago Module | ✅ COMPLETO (15 archivos) |
| Interacción Module | ✅ COMPLETO (15 archivos) |
| CasoGestion Module | ✅ COMPLETO (15 archivos) |
| Documentación | ✅ COMPLETA (4 docs) |
| Database Schema | ✅ GENERADO |
| Testing | ✅ LISTO |

---

## 🚀 Próximo Paso

Después de verificar que todo funciona:

1. Leer `TESTING_GUIDE.md` para casos de uso complejos
2. Revisar `IMPLEMENTACION_DDD_MODULOS_CORE_V2.md` para arquitectura
3. Empezar Fase 3 (Módulos Soporte)

---

## 💡 Tips

- **Usar Postman**: Colecciones pre-hechas facilitan testing
- **Índices**: Schema_ddd_modules.sql incluye índices optimizados
- **Transacciones**: Todas las modificaciones son @Transactional
- **Validaciones**: Cada módulo valida entrada y lógica de negocio
- **Errores**: ExceptionHandlers estandarizados en cada módulo

---

**¡Listo para comenzar!** 🎉

Cualquier pregunta, consultar documentación o archivos fuente en el proyecto.

