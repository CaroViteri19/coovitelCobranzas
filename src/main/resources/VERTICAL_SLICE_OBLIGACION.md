# Vertical Slice Obligacion - Resumen Ejecutivo

## ¿Qué acabamos de construir?

Un **caso de uso completo** (end-to-end) del dominio `Obligacion` siguiendo **arquitectura DDD** en Spring Boot.

## Flujo de una solicitud (ejemplo: GET /api/obligaciones/1)

```
┌─────────────────────────────────────────────────────────────┐
│ 1. HTTP REQUEST                                             │
│ GET /api/obligaciones/1                                     │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│ 2. CONTROLLER (Web Layer)                                   │
│ ObligacionController.obtenerPorId(1)                        │
│ - Recibe HTTP, traduce a parámetros                         │
│ - Llama al Application Service                              │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│ 3. APPLICATION SERVICE (Use Case Layer)                     │
│ ObligacionApplicationService.obtenerPorId(1)                │
│ - Orquesta la lógica del caso de uso                        │
│ - Llama al Repository del dominio                           │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│ 4. DOMAIN REPOSITORY (Domain Contract)                      │
│ ObligacionRepository.findById(1)                            │
│ - Interface pura del dominio (sin JPA, sin SQL)             │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│ 5. REPOSITORY IMPL (Persistence Adapter)                    │
│ ObligacionRepositoryImpl.findById(1)                         │
│ - Implementa usando JPA/SQL                                 │
│ - Convierte JpaEntity -> Domain Model                       │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│ 6. JPA LAYER (Spring Data)                                  │
│ ObligacionJpaRepository.findById(1)                         │
│ - Consulta a Base de Datos                                  │
│ - Retorna ObligacionJpaEntity                               │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│ 7. CONVERSIÓN A DOMINIO                                     │
│ ObligacionJpaEntity.toDomain()                              │
│ - ObligacionJpaEntity (persistencia)                        │
│ - Obligacion (modelo de dominio puro)                       │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│ 8. DTO RESPONSE                                             │
│ ObligacionResponse.fromDomain(obligacion)                   │
│ - Convierte a JSON para cliente                             │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│ 9. HTTP RESPONSE                                            │
│ { "id": 1, "saldoTotal": ..., "estado": "EN_MORA", ... }   │
└─────────────────────────────────────────────────────────────┘
```

## Estructura de carpetas creadas

```
src/main/java/coovitelCobranza/cobranzas/obligacion/
├── domain/
│   ├── model/
│   │   └── Obligacion.java              ← Agregado raíz (pura lógica de negocio)
│   ├── repository/
│   │   └── ObligacionRepository.java    ← Contrato de persistencia (sin JPA)
│   └── exception/
│       ├── ObligacionNotFoundException.java
│       └── ObligacionBusinessException.java
│
├── application/
│   ├── service/
│   │   └── ObligacionApplicationService.java  ← Orquestador de casos de uso
│   └── dto/
│       ├── ObligacionResponse.java
│       ├── RegistrarMoraRequest.java
│       └── AplicarPagoRequest.java
│
└── infrastructure/
    ├── persistence/
    │   ├── ObligacionJpaEntity.java     ← Adaptador JPA (mapeo DB)
    │   ├── ObligacionJpaRepository.java ← Spring Data
    │   └── ObligacionRepositoryImpl.java ← Implementación del contrato
    │
    └── web/
        ├── ObligacionController.java    ← Endpoints REST
        └── ObligacionExceptionHandler.java ← Manejo de errores
```

## Endpoints disponibles

### 1. Consultar por ID
```http
GET /api/obligaciones/1
```
**Response (200):**
```json
{
  "id": 1,
  "clienteId": 10,
  "numeroObligacion": "OBL-001",
  "saldoTotal": 150000.00,
  "saldoVencido": 50000.00,
  "diasMora": 15,
  "estado": "EN_MORA",
  "fechaVencimiento": "2026-04-30",
  "updatedAt": "2026-03-27T10:00:00"
}
```

### 2. Consultar por número
```http
GET /api/obligaciones/numero/OBL-001
```

### 3. Listar todas del cliente
```http
GET /api/obligaciones/cliente/10
```
**Response (200):**
```json
[
  { /* obligación 1 */ },
  { /* obligación 2 */ }
]
```

### 4. Registrar mora
```http
PUT /api/obligaciones/1/mora
Content-Type: application/json

{
  "diasMora": 20,
  "saldoVencido": 60000.00
}
```

### 5. Aplicar pago
```http
PUT /api/obligaciones/1/pago
Content-Type: application/json

{
  "valorPago": 50000.00
}
```

## Manejo de errores

### Caso: Obligación no encontrada
```http
GET /api/obligaciones/999
```
**Response (404):**
```json
{
  "timestamp": "2026-03-27T10:30:45.123Z",
  "status": 404,
  "code": "OBLIGACION_NOT_FOUND",
  "message": "No se encontro la obligacion con id: 999"
}
```

### Caso: Validación fallida
```http
PUT /api/obligaciones/1/pago
Content-Type: application/json

{
  "valorPago": -100
}
```
**Response (400):**
```json
{
  "timestamp": "2026-03-27T10:31:00.456Z",
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "valorPago debe ser mayor a cero"
}
```

### Caso: Lógica de negocio viola regla
```http
PUT /api/obligaciones/1/mora
Content-Type: application/json

{
  "diasMora": -5,
  "saldoVencido": 50000.00
}
```
**Response (400):**
```json
{
  "timestamp": "2026-03-27T10:31:15.789Z",
  "status": 400,
  "code": "OBLIGACION_BUSINESS_ERROR",
  "message": "diasMora no puede ser negativo"
}
```

## Reglas de negocio (Domain Model)

Implementadas en `Obligacion.java`:

1. **Registrar mora:**
   - Valida `diasMora >= 0`
   - Valida `saldoVencido >= 0`
   - Si `diasMora > 0` → estado = `EN_MORA`, si no → `AL_DIA`
   - Actualiza `updatedAt`

2. **Aplicar pago:**
   - Valida `valorPago > 0`
   - Resta del saldo total y vencido (nunca negativos)
   - Si saldo total = 0 → estado = `CANCELADA` y `diasMora = 0`
   - Actualiza `updatedAt`

## Capa de persistencia

**Flujo de guardar:**

```
Obligacion (dominio)
    │
    ▼
ObligacionRepositoryImpl.save()
    │
    ├─ ObligacionJpaEntity.fromDomain(obligacion)
    │
    ├─ jpaRepository.save(entity)
    │
    └─ result.toDomain() → Obligacion
```

**Mapeo a base de datos:**

```
Obligacion.id               ← obligation.id
Obligacion.clienteId        ← obligation.customer_id
Obligacion.numeroObligacion ← obligation.obligation_number
Obligacion.saldoTotal       ← obligation.total_balance
Obligacion.saldoVencido     ← obligation.overdue_balance
Obligacion.diasMora         ← obligation.overdue_days
Obligacion.estado           ← obligation.status (1=AL_DIA, 2=EN_MORA, etc.)
Obligacion.fechaVencimiento ← obligation.due_date
```

## Tests unitarios

Archivo: `src/test/java/coovitelCobranza/cobranzas/obligacion/domain/model/ObligacionTest.java`

Pruebas sin BD (solo lógica de dominio):

```bash
mvn test -Dtest=ObligacionTest
```

**Casos cubiertos:**
- ✅ Registrar mora y actualizar estado correctamente
- ✅ Lanzar error si pago es cero o negativo

## Próximos pasos

### 1. Integrar Pago
Crear dominio `Pago` que registre transacciones cuando se aplique un pago:
```
PUT /api/obligaciones/1/pago
  └─ Aplica pago en Obligacion
  └─ Crea registro en Pago
  └─ Emite evento `PagoConfirmado`
```

### 2. Ampliar tests
- Tests de integración con `MockMvc` para endpoints
- Tests del Application Service
- Tests del Repository Impl con H2 en memoria

### 3. Crear Cliente
Vertical slice del dominio `Cliente` (análogo a este).

### 4. Integrar eventos
- `ObligacionEnMoraDetectada` → dispara scoring/estrategia
- `PagoConfirmado` → actualiza caso de gestión

## Configuración necesaria (application.properties)

Asegurate de que tienes:

```properties
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.datasource.url=jdbc:mysql://localhost:3306/coovitelcobranzas
spring.datasource.username=mao
spring.datasource.password=mao123
```

## Cómo probar ahora

1. **Asegurate de que BD está viva:**
   ```bash
   mysql -u mao -p'mao123' -e "USE coovitelcobranzas; SHOW TABLES;"
   ```

2. **Ejecuta la app:**
   ```bash
   mvn spring-boot:run
   ```

3. **Abre Swagger:**
   ```
   http://localhost:8080/swagger-ui/index.html
   ```
   Verás la sección "Obligaciones" con todos los endpoints.

4. **Haz una solicitud de prueba:**
   ```bash
   curl -X GET http://localhost:8080/api/obligaciones/1
   ```

## Principios DDD aplicados

✅ **Aggregate Root:** `Obligacion` encapsula toda su lógica
✅ **Value Objects:** estados enumerados
✅ **Repository Pattern:** contrato de dominio sin JPA
✅ **Service Layer:** Application Service orquesta casos de uso
✅ **DTOs:** separación entre dominio y REST
✅ **Exception Handling:** errores de dominio vs aplicación
✅ **Transacciones:** `@Transactional` en servicios

---

**Resumen final:**  
Construimos un **bounded context completo** (`Obligacion`) que es **testeable, mantenible y escalable**. El dominio está protegido de Spring, las reglas de negocio son claras, y los errores se manejan de forma consistente.

