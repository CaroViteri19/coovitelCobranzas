# Guía de Pruebas — Carga Masiva de Clientes y Obligaciones

## Endpoint

```
POST http://localhost:8080/api/carga-batch
Content-Type: multipart/form-data
Param: file = <archivo .csv o .txt>
```

---

## Reglas de validación activas

| Campo           | Tipo     | Requerido | Regla                                              |
|-----------------|----------|-----------|----------------------------------------------------|
| TIPO_ID         | VARCHAR  | ✅        | Solo: `CC`, `NIT`, `CE`, `PA` (case-insensitive)  |
| NUM_DOCUMENTO   | VARCHAR  | ✅        | Máx 20 caracteres                                  |
| NOMBRE_COMPLETO | VARCHAR  | ✅        | Máx 120 caracteres                                 |
| NUM_OBLIGACION  | VARCHAR  | ✅        | Máx 30 caracteres                                  |
| SALDO_TOTAL     | DECIMAL  | ✅        | Formato numérico, máx 2 decimales                  |
| DIAS_MORA       | INTEGER  | ✅        | Entero ≥ 0                                         |
| FECHA_VENC      | DATE     | ✅        | Formato `YYYYMMDD`                                 |
| TELEFONO_1      | VARCHAR  | ✅        | Máx 15 caracteres                                  |
| EMAIL           | VARCHAR  | ❌        | Si viene, debe tener formato válido. Máx 80 chars  |
| CANAL_PREFERIDO | VARCHAR  | ❌        | Si viene, debe ser: `WhatsApp`, `SMS`, `Email`, `Voz` (sensible a mayúsculas) |

---

## PRUEBA 1 — Carga inicial (10 registros nuevos)

**Archivo:** `test_01_carga_inicial.csv`

**Qué verifica:**
- Todos los TIPO_ID válidos: `CC`, `NIT`, `CE`, `PA`
- Campos opcionales vacíos en varias filas
- `DIAS_MORA = 0` → estado AL_DIA
- `DIAS_MORA > 0` → estado EN_MORA
- Saldos con decimales
- Se crean 10 clientes y 10 obligaciones nuevas

**Respuesta esperada:**
```json
{
  "success": true,
  "totalRows": 10,
  "totalInserted": 10,
  "totalErrors": 0,
  "fileName": "test_01_carga_inicial.csv",
  "errors": []
}
```

**Verificar en BD:**
```sql
-- 10 clientes nuevos
SELECT tipo_documento, numero_documento, name_completo, telefono, email, ciudad, canal_preferido
FROM clientes
WHERE numero_documento IN (
  '10200300400','900123456','987654321','AB123456',
  '55566677','11122233','800567890','456789123','99887766','CD789012'
);

-- 10 obligaciones nuevas
SELECT obligation_number, total_balance, overdue_days, due_date, status, segmento, producto, codigo_agente
FROM obligation
WHERE obligation_number LIKE 'OBL-TEST-%';
```

---

## PRUEBA 2 — Actualización (upsert sobre datos existentes)

> ⚠️ **Ejecutar DESPUÉS de la Prueba 1.**

**Archivo:** `test_02_actualizacion.csv`

**Qué verifica:**
- 5 clientes que ya existen → se actualizan sus datos (nombre, teléfono, email, ciudad, canal)
- 5 obligaciones que ya existen (`OBL-TEST-001` a `OBL-TEST-005`) → se actualizan saldo, días mora, fecha venc
- 1 obligación nueva (`OBL-TEST-011`) para el cliente `10200300400` que YA EXISTE → se crea la nueva obligación vinculada al mismo cliente
- El cliente `10200300400` aparece 2 veces en el archivo → el cache evita queries dobles; se actualiza una sola vez

**Respuesta esperada:**
```json
{
  "success": true,
  "totalRows": 6,
  "totalInserted": 6,
  "totalErrors": 0,
  "errors": []
}
```

**Verificar cambios en BD:**
```sql
-- Clientes actualizados (email y ciudad cambiaron)
SELECT numero_documento, name_completo, email, ciudad, canal_preferido, updated_at
FROM clientes
WHERE numero_documento IN ('10200300400','900123456','987654321','AB123456','55566677');

-- Obligaciones actualizadas (saldo bajó para OBL-TEST-001)
SELECT obligation_number, total_balance, overdue_days, status, updated_at
FROM obligation
WHERE obligation_number IN ('OBL-TEST-001','OBL-TEST-002','OBL-TEST-003','OBL-TEST-004','OBL-TEST-005');

-- Obligación nueva del cliente existente
SELECT o.obligation_number, o.total_balance, c.numero_documento
FROM obligation o
JOIN clientes c ON o.customer_id = c.id
WHERE o.obligation_number = 'OBL-TEST-011';
```

---

## PRUEBA 3 — Archivo con errores (rechazo total)

**Archivo:** `test_03_errores_validacion.csv`

**Qué verifica (7 filas, todas con al menos 1 error):**

| Fila | Error esperado                                      |
|------|-----------------------------------------------------|
| 1    | `TIPO_ID` inválido: `TI` no está en CC/NIT/CE/PA   |
| 2    | `NUM_DOCUMENTO` vacío (campo requerido)             |
| 3    | `SALDO_TOTAL` = `NO_ES_NUMERO` + `DIAS_MORA` = `-5` (negativo) |
| 4    | `FECHA_VENC` = `20-05-2025` (formato incorrecto, debe ser YYYYMMDD) |
| 5    | `EMAIL` = `esto_no_es_un_email` (formato inválido)  |
| 6    | `CANAL_PREFERIDO` = `WHATSAPP` (case incorrecto, el válido es `WhatsApp`) |
| 7    | `NUM_DOCUMENTO` = `10200300400` duplicado (igual que fila 1 del archivo) |

**Comportamiento esperado:** La validación falla **antes de persistir nada** → rollback total → ningún registro se inserta.

**Respuesta esperada:**
```json
{
  "success": false,
  "totalRows": 7,
  "totalInserted": 0,
  "totalErrors": 7,
  "errors": [
    { "rowNumber": 1, "field": "TIPO_ID",        "message": "Valor inválido 'TI'. Permitidos: CC, NIT, CE, PA.", "severity": "ERROR" },
    { "rowNumber": 2, "field": "NUM_DOCUMENTO",  "message": "Campo obligatorio vacío.", "severity": "ERROR" },
    { "rowNumber": 3, "field": "SALDO_TOTAL",    "message": "Formato numérico inválido: 'NO_ES_NUMERO'.", "severity": "ERROR" },
    { "rowNumber": 3, "field": "DIAS_MORA",      "message": "Los días de mora no pueden ser negativos.", "severity": "ERROR" },
    { "rowNumber": 4, "field": "FECHA_VENC",     "message": "Formato de fecha inválido '20-05-2025'. Esperado: YYYYMMDD.", "severity": "ERROR" },
    { "rowNumber": 5, "field": "EMAIL",          "message": "Formato de email inválido: 'esto_no_es_un_email'.", "severity": "ERROR" },
    { "rowNumber": 6, "field": "CANAL_PREFERIDO","message": "Valor inválido 'WHATSAPP'. Permitidos: WhatsApp, SMS, Email, Voz.", "severity": "ERROR" },
    { "rowNumber": 7, "field": "NUM_DOCUMENTO",  "message": "Documento '10200300400' duplicado dentro del archivo.", "severity": "ERROR" }
  ]
}
```

**Verificar que NO se insertó nada:**
```sql
SELECT COUNT(*) FROM clientes WHERE numero_documento IN ('10200300400','55566677','800567890','456789123','99887766');
-- Debe retornar 0 si se ejecutó antes de la Prueba 1
-- Debe retornar el mismo conteo de antes si se ejecutó después
```

---

## PRUEBA 4 — Archivo TXT (separador pipe)

**Archivo:** `test_04_carga_inicial.txt`

> ⚠️ **Limpiar BD antes de ejecutar**, o cambiar los NUM_OBLIGACION para evitar conflictos con Prueba 1.

**Qué verifica:**
- Mismo contenido que Prueba 1 pero en formato TXT con separador `|`
- Sin encabezados (el TxtBatchParser no los espera)
- Campos opcionales vacíos representados como `||` (dos pipes seguidos)

**Respuesta esperada:** Igual a Prueba 1 — 10 exitosos, 0 errores.

---

## Cómo ejecutar con Postman / Insomnia

1. Método: `POST`
2. URL: `http://localhost:8080/api/carga-batch`
3. Body: `form-data`
4. Key: `file` → Type: **File** → seleccionar el archivo de prueba
5. Headers: NO agregar `Content-Type` manualmente (Postman lo pone automáticamente con el boundary)

## Cómo ejecutar con cURL

```bash
# Prueba 1 — Carga inicial
curl -X POST http://localhost:8080/api/carga-batch \
  -F "file=@test_01_carga_inicial.csv"

# Prueba 2 — Actualización
curl -X POST http://localhost:8080/api/carga-batch \
  -F "file=@test_02_actualizacion.csv"

# Prueba 3 — Errores
curl -X POST http://localhost:8080/api/carga-batch \
  -F "file=@test_03_errores_validacion.csv"

# Prueba 4 — TXT
curl -X POST http://localhost:8080/api/carga-batch \
  -F "file=@test_04_carga_inicial.txt"
```

---

## Consulta de auditoría post-pruebas

```sql
-- Ver todos los eventos de carga masiva
SELECT action, user, details, created_at
FROM audit_events
WHERE module = 'INTEGRATION'
  AND entity = 'CARGA_MASIVA'
ORDER BY created_at DESC;
```
