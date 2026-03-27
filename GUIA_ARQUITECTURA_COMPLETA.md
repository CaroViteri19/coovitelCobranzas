# 🏗️ GUÍA DE ARQUITECTURA - TRADUCCIÓN A INGLÉS

## 📚 ÍNDICE RÁPIDO

1. [¿Cómo funciona el sistema?](#flujo)
2. [Qué es cada archivo nuevo](#archivos)
3. [Cómo usar los nuevos servicios](#uso)
4. [Ejemplos prácticos](#ejemplos)

---

## 🔄 FLUJO COMPLETO DEL SISTEMA {#flujo}

### Diagrama: De HTTP Request a Respuesta

```
┌─────────────────────────────────────────────────────────────────┐
│ 1. CLIENTE HTTP (Postman, App, Browser)                         │
│    Envía: POST /api/v1/cases                                    │
│    Con JSON: { obligationId: 123, priority: "HIGH" }            │
└──────────────────┬──────────────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────────────┐
│ 2. CONTROLLER (CasoGestionController)                           │
│    ✓ Recibe el JSON                                             │
│    ✓ Valida que sea CreateCaseRequest                           │
│    ✓ Llama al servicio                                          │
└──────────────────┬──────────────────────────────────────────────┘
                   │
        controller.create(request)
                   │
                   ▼
┌─────────────────────────────────────────────────────────────────┐
│ 3. SERVICE (CaseApplicationService)                             │
│    ✓ Valida datos de negocio                                   │
│    ✓ Crea el modelo de dominio (Case)                           │
│    ✓ Guarda en la base de datos (via Repository)               │
│    ✓ Convierte a DTO para responder                             │
└──────────────────┬──────────────────────────────────────────────┘
                   │
        service.createCase(request)
                   │
                   ▼
┌─────────────────────────────────────────────────────────────────┐
│ 4. DOMAIN MODEL (Case)                                          │
│    ✓ Contiene lógica pura de negocio                            │
│    ✓ No sabe de HTTP, BD, nada externo                          │
│    ✓ Solo sabe de casos de cobranza                             │
└──────────────────┬──────────────────────────────────────────────┘
                   │
        Case.create(obligationId, priority)
                   │
                   ▼
┌─────────────────────────────────────────────────────────────────┐
│ 5. REPOSITORY (CasoGestionRepository)                           │
│    ✓ Guarda el caso en MySQL                                    │
│    ✓ No tiene lógica de negocio                                 │
│    ✓ Solo sabe guardar/obtener datos                            │
└──────────────────┬──────────────────────────────────────────────┘
                   │
                   ▼
        ¡Guardado en base de datos!
                   │
                   ▼
┌─────────────────────────────────────────────────────────────────┐
│ 6. RESPUESTA HTTP                                               │
│    Status: 201 CREATED                                          │
│    Body: { id: 999, obligationId: 123, priority: "HIGH", ... }  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 📁 QUÉ ES CADA ARCHIVO NUEVO {#archivos}

### MODELOS DE DOMINIO (Lo más importante)

#### `Case.java` - Representa un CASO de cobranza
```
PARA QUÉ SIRVE:
└─ Almacenar información de un caso: ID, obligación, prioridad, estado, asesor
└─ Cambiar de estado (abierto → en gestión → cerrado)
└─ Asignar un asesor responsable
└─ Programar siguiente contacto con el cliente

MÉTODOS:
└─ create()              → Crear un nuevo caso
└─ assignAdvisor()       → Asignar asesor responsable
└─ scheduleNextAction()  → Programar siguiente contacto
└─ close()              → Cerrar el caso
```

#### `Payment.java` - Representa un PAGO
```
PARA QUÉ SIRVE:
└─ Registrar cuándo un cliente pagó
└─ Guardar monto, método de pago, referencia del banco
└─ Cambiar estado (pendiente → confirmado → rechazado)

MÉTODOS:
└─ createPending()  → Crear pago en espera
└─ confirm()        → Confirmar que se recibió el dinero
└─ reject()         → Rechazar si hay error
```

#### `Interaction.java` - Representa un CONTACTO con cliente
```
PARA QUÉ SIRVE:
└─ Registrar que intentamos contactar a un cliente
└─ Guardar qué canal usamos (SMS, WhatsApp, Email, Llamada)
└─ Registrar resultado (entregado, leído, fallido)

MÉTODOS:
└─ create()           → Registrar nuevo contacto
└─ markDelivered()    → Marcar como entregado
└─ markRead()         → Marcar como leído
└─ markFailed()       → Marcar como fallido
```

#### `ScoringSegmentation.java` - Resultado del ANÁLISIS de crédito
```
PARA QUÉ SIRVE:
└─ Guardar puntuación de riesgo del cliente (0-100)
└─ Guardar en qué categoría cae (bajo/medio/alto riesgo)
└─ Guardar versión del modelo IA usado
└─ Guardar razón del resultado

MÉTODOS:
└─ create()       → Crear nuevo resultado de análisis
└─ reconstruct()  → Recuperar desde base de datos
```

---

### SERVICIOS DE APLICACIÓN (Coordinadores)

#### `CaseApplicationService.java`
```
PARA QUÉ SIRVE:
└─ Coordinar todo lo relacionado con casos
└─ Valida datos que vienen del HTTP
└─ Llama al modelo de dominio
└─ Guarda en base de datos
└─ Convierte respuestas a JSON

MÉTODOS PRINCIPALES:
└─ createCase(request)      → Crear caso desde HTTP
└─ getById(id)              → Obtener caso por ID
└─ listPending()            → Listar casos sin asignar
└─ assignAdvisor(id, req)   → Asignar asesor
└─ scheduleAction(id, req)  → Programar contacto
└─ closeCase(id)            → Cerrar caso
```

#### Servicios similares para Payment, Interaction, ScoringSegmentation
```
PaymentApplicationService
InteractionApplicationService
ScoringSegmentationApplicationService
```

---

### DATA TRANSFER OBJECTS (DTOs) - Para HTTP

#### Request DTOs: Para RECIBIR datos
```
CreateCaseRequest
├─ obligationId: Long (ID de la deuda)
└─ priority: String (BAJA/MEDIA/ALTA/CRÍTICA)

AssignAdvisorRequest
└─ advisor: String (nombre del asesor)

ScheduleActionRequest
└─ dateTime: LocalDateTime (cuándo contactar)

CreatePaymentRequest
├─ obligationId: Long
├─ amount: BigDecimal (monto a pagar)
├─ externalReference: String (referencia del banco)
└─ method: String (PSE/TARJETA/TRANSFERENCIA)
```

#### Response DTOs: Para ENVIAR datos
```
CaseResponse
├─ id: Long
├─ obligationId: Long
├─ priority: String
├─ status: String
├─ assignedAdvisor: String
└─ nextActionAt: LocalDateTime

PaymentResponse, InteractionResponse, ScoringSegmentationResponse
(Similar estructura con campos del dominio)
```

---

### EXCEPCIONES (Para manejar errores)

```
CaseNotFoundException
└─ Se lanza cuando buscas un caso que no existe

CaseBusinessException
└─ Se lanza cuando hay error de lógica de negocio
  └─ Ej: Intentar asignar asesor vacío

PaymentNotFoundException, PaymentBusinessException
InteractionNotFoundException, InteractionBusinessException
ScoringSegmentationNotFoundException, ScoringSegmentationBusinessException
```

---

## 💡 CÓMO USAR LOS NUEVOS SERVICIOS {#uso}

### En un Controller:

```java
@RestController
@RequestMapping("/api/v1/cases")
public class CasoGestionController {
    
    // El servicio se inyecta automáticamente
    private final CaseApplicationService caseApplicationService;
    
    public CasoGestionController(CaseApplicationService caseApplicationService) {
        this.caseApplicationService = caseApplicationService;
    }
    
    // Endpoint para crear caso
    @PostMapping
    public ResponseEntity<CaseResponse> create(
            @RequestBody CreateCaseRequest request) {
        // El servicio hace todo el trabajo
        CaseResponse response = caseApplicationService.createCase(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    // Endpoint para obtener caso
    @PostMapping("/search/id")
    public ResponseEntity<CaseResponse> getById(
            @RequestBody GetCaseByIdRequest request) {
        CaseResponse response = caseApplicationService.getById(request.caseId());
        return ResponseEntity.ok(response);
    }
    
    // Endpoint para asignar asesor
    @PostMapping("/assign-advisor")
    public ResponseEntity<CaseResponse> assignAdvisor(
            @RequestBody AssignAdvisorByCaseRequest request) {
        CaseResponse response = caseApplicationService.assignAdvisor(
            request.caseId(),
            new AssignAdvisorRequest(request.advisor())
        );
        return ResponseEntity.ok(response);
    }
}
```

---

## 📋 EJEMPLOS PRÁCTICOS {#ejemplos}

### Ejemplo 1: Crear un caso de cobranza

```bash
# Request HTTP
POST /api/v1/cases
Content-Type: application/json

{
  "obligationId": 123,
  "priority": "HIGH"
}

# ¿Qué pasa internamente?
1. Controller recibe el JSON
2. Lo convierte a CreateCaseRequest
3. Llama a service.createCase(request)
4. Service crea el modelo: Case.create(123, Priority.HIGH)
5. El Case valida que todo esté OK
6. Service guarda en BD via repository
7. Service convierte a JSON y devuelve

# Response (201 Created)
{
  "id": 999,
  "obligationId": 123,
  "priority": "HIGH",
  "status": "OPEN",
  "assignedAdvisor": null,
  "nextActionAt": null,
  "updatedAt": "2026-03-27T16:30:00"
}
```

### Ejemplo 2: Asignar asesor a un caso

```bash
# Request HTTP
POST /api/v1/cases/assign-advisor
Content-Type: application/json

{
  "caseId": 999,
  "advisor": "Juan Pérez"
}

# ¿Qué pasa?
1. Controller recibe caseId=999, advisor="Juan Pérez"
2. Service obtiene el caso: Case caso = repository.findById(999)
3. Llama al modelo: caso.assignAdvisor("Juan Pérez")
4. El Case cambia estado: OPEN → IN_MANAGEMENT
5. Service guarda cambios
6. Devuelve el caso actualizado

# Response (200 OK)
{
  "id": 999,
  "obligationId": 123,
  "priority": "HIGH",
  "status": "IN_MANAGEMENT",        ← Cambió!
  "assignedAdvisor": "Juan Pérez",  ← Asignado!
  "nextActionAt": null,
  "updatedAt": "2026-03-27T16:35:00"
}
```

### Ejemplo 3: Registrar un pago

```bash
# Request HTTP
POST /api/v1/payments
Content-Type: application/json

{
  "obligationId": 123,
  "amount": 500000,
  "externalReference": "PSE-REF-12345",
  "method": "PSE"
}

# ¿Qué pasa?
1. Service crea el pago: Payment.createPending(123, 500000, "PSE-REF-12345", PaymentMethod.PSE)
2. El Payment comienza en estado PENDING
3. Se guarda en BD
4. Devuelve la respuesta

# Más tarde, cuando llega confirmación del banco:
POST /api/v1/payments/confirm
{
  "reference": "PSE-REF-12345"
}

# Service busca el pago y llama:
# pago.confirm() → Cambia estado a CONFIRMED
```

---

## 🎓 CONCEPTOS CLAVE

### ¿Qué es un SERVICE?
Un service es el "gerente" que:
- Recibe solicitudes (DTOs)
- Valida que los datos sean correctos
- Llama al modelo de dominio para hacer el trabajo
- Guarda resultados en BD
- Devuelve respuesta al controller

### ¿Qué es un MODEL (Domain)?
El modelo es donde vive la **lógica de negocio pura**:
- No sabe de HTTP, BD, ni nada externo
- Solo sabe de su propia responsabilidad (Ej: Case)
- Valida reglas de negocio
- Cambia de estado cuando es apropiado

### ¿Qué es un DTO?
Un DTO (Data Transfer Object) es un "sobre" para:
- RECIBIR datos del HTTP en request DTOs
- ENVIAR datos al HTTP en response DTOs
- Es solo datos, sin lógica

### ¿Por qué tantas capas?
```
Separación de Responsabilidades:
├─ Controller: Solo hablar HTTP
├─ Service: Solo orquestar
├─ Domain: Solo lógica de negocio
└─ Repository: Solo guardar/obtener datos

Beneficios:
├─ Fácil de probar
├─ Fácil de cambiar
├─ Reutilizable
└─ Mantenible
```

---

## 🔗 RELACIONES ENTRE ARCHIVOS

```
CasoGestionController (recibe HTTP)
    ↓
    └→ CaseApplicationService (orquesta)
        ├→ Case.java (lógica de negocio)
        ├→ CasoGestionRepository (guarda datos)
        └→ CaseResponse (devuelve datos)

CreateCaseRequest (recibe del cliente)
    ↓
    └→ CaseApplicationService
        ├→ Case.create() (crea modelo)
        └→ CaseResponse.fromDomain() (convierte)
```

---

**¡Espero que ahora esté claro qué hace cada cosa! 😊**

Si necesitas más detalles de algún archivo específico, dímelo y agrego más comentarios.

