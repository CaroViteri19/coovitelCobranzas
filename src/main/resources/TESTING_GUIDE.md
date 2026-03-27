# Testing Guide - Arquitectura DDD Módulos Core

## 📋 Guía de Testing Completa

Este documento describe cómo probar manualmente todos los endpoints implementados en los 4 módulos Core.

---

## 🚀 Prerrequisitos

1. **Base de Datos**: Ejecutar scripts en `src/main/resources/db/`
2. **Servidor**: `mvn spring-boot:run` (en puerto 8080 por defecto)
3. **Cliente HTTP**: Postman, curl o Thunder Client

---

## 🧪 Testing por Módulo

### 1️⃣ CLIENTE - Testing

#### 1.1 Crear Cliente
```bash
curl -X POST http://localhost:8080/api/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "tipoDocumento": "CC",
    "numeroDocumento": "12345678",
    "nombreCompleto": "Juan Pérez",
    "telefono": "3001234567",
    "email": "juan@example.com"
  }'
```

**Response esperado (201 CREATED)**:
```json
{
  "id": 1,
  "tipoDocumento": "CC",
  "numeroDocumento": "12345678",
  "nombreCompleto": "Juan Pérez",
  "telefono": "3001234567",
  "email": "juan@example.com",
  "aceptaWhatsApp": false,
  "aceptaSms": false,
  "aceptaEmail": false,
  "updatedAt": "2026-03-27T10:44:07"
}
```

#### 1.2 Obtener Cliente por ID
```bash
curl -X GET http://localhost:8080/api/clientes/1
```

#### 1.3 Obtener Cliente por Documento
```bash
curl -X GET http://localhost:8080/api/clientes/documento/CC/12345678
```

#### 1.4 Actualizar Contacto
```bash
curl -X PUT http://localhost:8080/api/clientes/1/contacto \
  -H "Content-Type: application/json" \
  -d '{
    "telefono": "3009876543",
    "email": "juan.nuevo@example.com"
  }'
```

#### 1.5 Actualizar Consentimientos
```bash
curl -X PUT http://localhost:8080/api/clientes/1/consentimientos \
  -H "Content-Type: application/json" \
  -d '{
    "aceptaWhatsApp": true,
    "aceptaSms": true,
    "aceptaEmail": false
  }'
```

**Validaciones a Probar**:
- ✅ No crear cliente con documento duplicado
- ✅ No obtener cliente que no existe (404)
- ✅ Actualizar contacto con null parcial

---

### 2️⃣ PAGO - Testing

#### Precondición: Crear Obligación (módulo ya existente)
```bash
curl -X POST http://localhost:8080/api/obligaciones \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 1,
    "numeroObligacion": "OBL-001",
    "saldoTotal": 5000.00
  }'
```

#### 2.1 Crear Pago
```bash
curl -X POST http://localhost:8080/api/pagos \
  -H "Content-Type: application/json" \
  -d '{
    "obligacionId": 1,
    "valor": 500.00,
    "referenciaExterna": "PAG-20260327-001",
    "metodo": "PSE"
  }'
```

**Response esperado (201 CREATED)**:
```json
{
  "id": 1,
  "obligacionId": 1,
  "valor": 500.00,
  "referenciaExterna": "PAG-20260327-001",
  "metodo": "PSE",
  "estado": "PENDIENTE",
  "confirmadoAt": null,
  "createdAt": "2026-03-27T10:44:07"
}
```

#### 2.2 Obtener Pago por ID
```bash
curl -X GET http://localhost:8080/api/pagos/1
```

#### 2.3 Obtener Pago por Referencia
```bash
curl -X GET http://localhost:8080/api/pagos/referencia/PAG-20260327-001
```

#### 2.4 Listar Pagos por Obligación
```bash
curl -X GET http://localhost:8080/api/pagos/obligacion/1
```

#### 2.5 Confirmar Pago
```bash
curl -X POST http://localhost:8080/api/pagos/confirmar \
  -H "Content-Type: application/json" \
  -d '{
    "referencia": "PAG-20260327-001"
  }'
```

**Response esperado (200 OK)**: estado ahora es "CONFIRMADO", confirmadoAt tiene timestamp.

#### 2.6 Rechazar Pago
```bash
curl -X POST http://localhost:8080/api/pagos \
  -H "Content-Type: application/json" \
  -d '{
    "obligacionId": 1,
    "valor": 200.00,
    "referenciaExterna": "PAG-20260327-002",
    "metodo": "TARJETA"
  }'

curl -X PUT http://localhost:8080/api/pagos/2/rechazar
```

**Validaciones a Probar**:
- ✅ No crear pago con referencia duplicada
- ✅ No confirmar pago que no es PENDIENTE (debe fallar)
- ✅ No rechazar pago que no es PENDIENTE
- ✅ Validar valor > 0
- ✅ Validar método válido (PSE, TARJETA, TRANSFERENCIA, OFICINA)

---

### 3️⃣ INTERACCION - Testing

#### Precondición: Crear Caso de Gestión (ver módulo CASOGESTION)

#### 3.1 Crear Interacción
```bash
curl -X POST http://localhost:8080/api/interacciones \
  -H "Content-Type: application/json" \
  -d '{
    "casoGestionId": 1,
    "canal": "WHATSAPP",
    "plantilla": "Hola, notificamos que su deuda vence el 30 de marzo"
  }'
```

**Response esperado (201 CREATED)**:
```json
{
  "id": 1,
  "casoGestionId": 1,
  "canal": "WHATSAPP",
  "plantilla": "Hola, notificamos que su deuda vence el 30 de marzo",
  "resultado": "PENDIENTE",
  "createdAt": "2026-03-27T10:44:07"
}
```

#### 3.2 Obtener Interacción por ID
```bash
curl -X GET http://localhost:8080/api/interacciones/1
```

#### 3.3 Listar Interacciones por Caso
```bash
curl -X GET http://localhost:8080/api/interacciones/caso/1
```

#### 3.4 Actualizar Resultado a ENTREGADA
```bash
curl -X PUT http://localhost:8080/api/interacciones/1/resultado \
  -H "Content-Type: application/json" \
  -d '{
    "resultado": "ENTREGADA"
  }'
```

#### 3.5 Actualizar Resultado a LEIDA
```bash
curl -X PUT http://localhost:8080/api/interacciones/1/resultado \
  -H "Content-Type: application/json" \
  -d '{
    "resultado": "LEIDA"
  }'
```

**Validaciones a Probar**:
- ✅ Validar canal válido (SMS, WHATSAPP, EMAIL, VOZ)
- ✅ Validar resultado válido
- ✅ No obtener interacción inexistente

---

### 4️⃣ CASOGESTION - Testing

#### 4.1 Crear Caso de Gestión
```bash
curl -X POST http://localhost:8080/api/casos-gestion \
  -H "Content-Type: application/json" \
  -d '{
    "obligacionId": 1,
    "prioridad": "ALTA"
  }'
```

**Response esperado (201 CREATED)**:
```json
{
  "id": 1,
  "obligacionId": 1,
  "prioridad": "ALTA",
  "estado": "ABIERTO",
  "asesorAsignado": null,
  "proximaAccionAt": null,
  "updatedAt": "2026-03-27T10:44:07"
}
```

#### 4.2 Obtener Caso por ID
```bash
curl -X GET http://localhost:8080/api/casos-gestion/1
```

#### 4.3 Listar Casos Pendientes
```bash
curl -X GET http://localhost:8080/api/casos-gestion/pendientes
```

Expected: Retorna array de casos con estado ABIERTO o EN_GESTION.

#### 4.4 Asignar Asesor
```bash
curl -X PUT http://localhost:8080/api/casos-gestion/1/asesor \
  -H "Content-Type: application/json" \
  -d '{
    "asesor": "Maria González"
  }'
```

**Response esperado**: estado cambia a EN_GESTION, asesorAsignado = "Maria González"

#### 4.5 Programar Próxima Acción
```bash
curl -X PUT http://localhost:8080/api/casos-gestion/1/proximo-accion \
  -H "Content-Type: application/json" \
  -d '{
    "fechaHora": "2026-03-28T14:30:00"
  }'
```

#### 4.6 Cerrar Caso
```bash
curl -X PUT http://localhost:8080/api/casos-gestion/1/cerrar
```

**Response esperado**: estado cambia a CERRADO.

**Validaciones a Probar**:
- ✅ Validar prioridad válida (BAJA, MEDIA, ALTA, CRITICA)
- ✅ No asignar asesor vacío
- ✅ No obtener caso inexistente

---

## 🧠 Flujos de Negocio Completos

### Flujo 1: Crear Cliente → Obligación → Pago → Confirmar
```
1. POST /api/clientes → CLIENTE creado (id=1)
2. POST /api/obligaciones → OBLIGACION creada (id=1, clienteId=1)
3. POST /api/pagos → PAGO creado (id=1, estado=PENDIENTE)
4. POST /api/pagos/confirmar → PAGO confirmado (estado=CONFIRMADO)
5. GET /api/pagos/1 → Verificar confirmadoAt != null
```

### Flujo 2: Crear Caso → Asignar Asesor → Crear Interacción
```
1. POST /api/casos-gestion → CASO creado (id=1, estado=ABIERTO)
2. PUT /api/casos-gestion/1/asesor → CASO asignado (estado=EN_GESTION)
3. POST /api/interacciones → INTERACCION creada (casoGestionId=1)
4. PUT /api/interacciones/1/resultado → INTERACCION entregada
5. PUT /api/casos-gestion/1/cerrar → CASO cerrado
```

### Flujo 3: Error - Pago con Referencia Duplicada
```
1. POST /api/pagos → PAGO1 creado (referencia="REF-001")
2. POST /api/pagos → INTENTA crear PAGO2 (referencia="REF-001")
   → Esperado: 400 BAD_REQUEST
   {
     "timestamp": "2026-03-27T10:44:07",
     "status": 400,
     "error": "Error de negocio",
     "message": "Pago ya existe con referencia: REF-001"
   }
```

---

## 📊 Matriz de Validación

| Módulo | Funcionalidad | Status | Notas |
|--------|---------------|--------|-------|
| Cliente | CRUD Básico | ✅ | |
| Cliente | Documento Único | ✅ | Validar en BD |
| Cliente | Consentimientos | ✅ | Booleanos |
| Pago | CRUD Básico | ✅ | |
| Pago | Confirmar | ✅ | Solo PENDIENTE |
| Pago | Rechazar | ✅ | Solo PENDIENTE |
| Pago | Referencia Única | ✅ | Validar en BD |
| Interacción | CRUD Básico | ✅ | |
| Interacción | Actualizar Resultado | ✅ | 3 resultados cubiertos |
| Interaccion | Validar Canal | ✅ | 4 canales soportados |
| CasoGestion | CRUD Básico | ✅ | |
| CasoGestion | Listar Pendientes | ✅ | Query ABIERTO+EN_GESTION |
| CasoGestion | Asignar Asesor | ✅ | Cambia estado |
| CasoGestion | Cerrar | ✅ | Solo para casos EN_GESTION |

---

## 🐛 Casos de Error a Probar

### Casos que deben retornar 404 NOT_FOUND

```bash
# Cliente
curl -X GET http://localhost:8080/api/clientes/999

# Pago
curl -X GET http://localhost:8080/api/pagos/999

# Interacción
curl -X GET http://localhost:8080/api/interacciones/999

# CasoGestion
curl -X GET http://localhost:8080/api/casos-gestion/999
```

### Casos que deben retornar 400 BAD_REQUEST

```bash
# Pago - Referencia duplicada
curl -X POST http://localhost:8080/api/pagos \
  -H "Content-Type: application/json" \
  -d '{
    "obligacionId": 1,
    "valor": 500.00,
    "referenciaExterna": "PAG-DUPLICADA",
    "metodo": "PSE"
  }'
# (Crear dos veces)

# Interacción - Canal inválido
curl -X POST http://localhost:8080/api/interacciones \
  -H "Content-Type: application/json" \
  -d '{
    "casoGestionId": 1,
    "canal": "TELEGRAM",
    "plantilla": "..."
  }'

# CasoGestion - Prioridad inválida
curl -X POST http://localhost:8080/api/casos-gestion \
  -H "Content-Type: application/json" \
  -d '{
    "obligacionId": 1,
    "prioridad": "URGENTISIMA"
  }'
```

---

## 📝 Script de Testing Completo (Bash)

```bash
#!/bin/bash

BASE_URL="http://localhost:8080"

echo "=== Testing Cliente ==="
CLIENTE_ID=$(curl -s -X POST $BASE_URL/api/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "tipoDocumento": "CC",
    "numeroDocumento": "123456",
    "nombreCompleto": "Test User",
    "telefono": "3001234567",
    "email": "test@example.com"
  }' | jq -r '.id')

echo "Cliente creado: $CLIENTE_ID"

echo "=== Testing Pago ==="
PAGO_ID=$(curl -s -X POST $BASE_URL/api/pagos \
  -H "Content-Type: application/json" \
  -d '{
    "obligacionId": 1,
    "valor": 1000.00,
    "referenciaExterna": "TEST-'$(date +%s)'",
    "metodo": "PSE"
  }' | jq -r '.id')

echo "Pago creado: $PAGO_ID"

echo "=== Testing CasoGestion ==="
CASO_ID=$(curl -s -X POST $BASE_URL/api/casos-gestion \
  -H "Content-Type: application/json" \
  -d '{
    "obligacionId": 1,
    "prioridad": "MEDIA"
  }' | jq -r '.id')

echo "Caso creado: $CASO_ID"

echo "=== Testing Interaccion ==="
INTER_ID=$(curl -s -X POST $BASE_URL/api/interacciones \
  -H "Content-Type: application/json" \
  -d '{
    "casoGestionId": '$CASO_ID',
    "canal": "SMS",
    "plantilla": "Test message"
  }' | jq -r '.id')

echo "Interacción creada: $INTER_ID"

echo "=== All Tests Completed ==="
```

---

## 🎯 Conclusión

Todos los endpoints están implementados y listos para testing. Se recomienda usar Postman con variables de entorno para facilitar el testing secuencial de flujos complejos.

**Status**: ✅ **LISTO PARA TESTING MANUAL Y AUTOMATIZADO**

