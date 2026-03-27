# English Controller Refactoring Template

Use this template to refactor remaining controllers from Spanish to English while moving sensitive data to request body.

---

## Pattern: Secure Search via POST + DTO

### BEFORE (❌ Insecure)
```java
@GetMapping("/obligacion/{obligacionId}")
public ResponseEntity<ObligacionResponse> obtenerPorId(@PathVariable Long obligacionId) {
    // Problem: obligacionId visible in URL/logs/history
    return ResponseEntity.ok(service.obtenerPorId(obligacionId));
}
```

### AFTER (✅ Secure)
```java
// Step 1: Create DTO
public record GetObligationByIdRequest(Long obligationId) {}

// Step 2: Update Controller
@PostMapping("/search/id")
public ResponseEntity<ObligationResponse> getById(@RequestBody GetObligationByIdRequest request) {
    return ResponseEntity.ok(service.getById(request.obligationId()));
}
```

---

## Mapping Guide

### Naming Conventions (Spanish → English)

| Spanish | English | Context |
|---------|---------|---------|
| Cliente | Client | Customer/User entity |
| Obligacion | Obligation | Debt/Payment obligation |
| Pago | Payment | Transaction |
| CasoGestion | Case | Case management |
| Interaccion | Interaction | Communication event |
| Estrategia | Strategy | Billing strategy |
| Politica | Policy | Rules/conditions |
| Scoring | Scoring | Risk assessment (no change) |
| Auditoria | Audit | Tracking/logging |
| Orquestacion | Orchestration | Channel management |
| Segmentacion | Segmentation | Customer grouping |
| CreaAlgo | Create[Noun] | Create operation |
| Actualizar | Update | Modify operation |
| Listar | List | Retrieve multiple |
| Obtener | Get | Retrieve single |
| Registrar | Register | Log/record |

---

## DTO Naming Pattern

```java
// For GET by ID (becomes POST with DTO)
public record Get[Resource]ByIdRequest(Long id) {}

// For search/filter
public record List[Resource]By[Field]Request([Type] [field]) {}

// For create
public record Create[Resource]Request([fields]) {}

// For update
public record Update[Resource]Request([fields]) {}

// Response (always from domain)
public record [Resource]Response([fields]) {
    public static [Resource]Response fromDomain([Resource] entity) { ... }
}
```

---

## Method Naming Pattern

```java
@PostMapping("/create")
public ResponseEntity<[Resource]Response> create(@RequestBody Create[Resource]Request req) { ... }

@GetMapping("/{id}")
public ResponseEntity<[Resource]Response> getById(@PathVariable Long id) { ... }

@PostMapping("/search/[field]")
public ResponseEntity<List<[Resource]Response>> listBy[Field](@RequestBody List[Resource]By[Field]Request req) { ... }

@PutMapping("/{id}")
public ResponseEntity<[Resource]Response> update(@PathVariable Long id, @RequestBody Update[Resource]Request req) { ... }

@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable Long id) { ... }
```

---

## Controllers Checklist

### Tier 1: CRITICAL (High sensitivity)
- [ ] **ClienteController** - Contains: DNI, names, contact info
  - GET `/clientes/{clienteId}` → POST `/clientes/search/id`
  - GET `/clientes` (list) → POST `/clientes/search` with filters in body

- [ ] **PagoController** - Contains: Transaction IDs, amounts
  - GET `/pagos/{pagoId}` → POST `/pagos/search/id`
  - GET `/pagos/obligacion/{obligacionId}` → POST `/pagos/search/obligation`

### Tier 2: HIGH
- [ ] **CasoGestionController** - Contains: Case assignment, case IDs
- [ ] **InteraccionController** - Contains: Communication history
- [ ] **ObligacionController** - Contains: Obligation IDs, customer debt

### Tier 3: MEDIUM
- [ ] **EstrategiaController** - Strategy configs
- [ ] **PoliticaController** - Policy rules
- [ ] **AuthController** - User authentication (already handled separately)

---

## Service Method Changes

When controllers change from GET to POST, **service method names stay the same** but DTOs change signature:

### BEFORE
```java
public ObligacionResponse obtenerPorId(Long obligacionId) { ... }
```

### AFTER (signature compatible)
```java
public ObligacionResponse obtenerPorId(Long obligacionId) { ... }
// Same method, called with: request.obligacionId()
```

**No changes needed in application services!** Only controller + DTO changes.

---

## API Versioning Scheme

All endpoints use `/api/v1/`:

```
❌ OLD:  /api/auditoria/eventos
✅ NEW:  /api/v1/audit/events

❌ OLD:  /api/clientes
✅ NEW:  /api/v1/clients

❌ OLD:  /api/pagos
✅ NEW:  /api/v1/payments

❌ OLD:  /api/obligaciones
✅ NEW:  /api/v1/obligations
```

---

## JavaDoc Template

```java
/**
 * [Resource] REST Controller
 * 
 * Security Measures:
 * - All sensitive data (IDs, personal info) transmitted in encrypted request body (HTTPS)
 * - Path parameters used only for immutable identifiers (returned IDs are safe)
 * - Search operations use POST to prevent caching of sensitive queries
 */
@RestController
@RequestMapping("/api/v1/[resources]")
@CrossOrigin(origins = "*", maxAge = 3600)
public class [Resource]Controller {

    private final [Resource]ApplicationService service;

    public [Resource]Controller([Resource]ApplicationService service) {
        this.service = service;
    }

    /**
     * Create a new [resource].
     * 
     * @param request creation parameters
     * @return 201 Created with [resource] ID and details
     * @throws [Exception] if validation fails
     */
    @PostMapping("/create")
    public ResponseEntity<[Resource]Response> create(@RequestBody Create[Resource]Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    /**
     * Get [resource] by ID.
     * 
     * Note: ID is in URL path (immutable identifier), not sensitive search term.
     * 
     * @param id resource ID
     * @return 200 OK with [resource] details
     * @throws [NotFound] if ID doesn't exist
     */
    @GetMapping("/{id}")
    public ResponseEntity<[Resource]Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    /**
     * Search [resources] by [field].
     * 
     * Security: Search parameters are in encrypted request body (HTTPS).
     * 
     * @param request contains [field] and optional filters
     * @return 200 OK with matching [resource] list
     */
    @PostMapping("/search/[field]")
    public ResponseEntity<List<[Resource]Response>> listBy[Field](@RequestBody List[Resource]By[Field]Request request) {
        return ResponseEntity.ok(service.listBy[Field](request.[field]()));
    }
}
```

---

## Exception Handler Template

Each controller needs a clean exception handler (in English):

```java
/**
 * [Resource] REST Exception Handler
 */
@RestControllerAdvice(basePackages = "cooviteCobranza.cobranzas.[resource]")
public class [Resource]ExceptionHandler {

    @ExceptionHandler([Resource]NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handle[Resource]NotFound([Resource]NotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "[Resource] not found");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler([Resource]BusinessException.class)
    public ResponseEntity<Map<String, Object>> handle[Resource]Business([Resource]BusinessException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Business rule violation");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
```

---

## Quick Start Checklist (per controller)

- [ ] Create DTOs (GetByIdRequest, ListBy[Field]Request, Response)
- [ ] Rename Controller (Spanish → English)
- [ ] Change @RequestMapping path (lowercase, v1 version)
- [ ] Convert GET endpoints to POST where they receive IDs
- [ ] Update method names (Spanish → English)
- [ ] Add JavaDoc with security notes
- [ ] Create/update ExceptionHandler (English)
- [ ] Compile (`mvnw compile`)
- [ ] Test with new endpoints
- [ ] Update API docs (Swagger/OpenAPI)

---

## Example: Complete Cliente → Client Refactoring

**Before**:
```java
@GetMapping("/clientes/{clienteId}")
public ResponseEntity<ClienteResponse> obtenerPorId(@PathVariable Long clienteId) { ... }
```

**After**:
```java
// DTO (new file)
public record GetClientByIdRequest(Long clientId) {}

// Controller (updated)
@PostMapping("/search/id")
public ResponseEntity<ClientResponse> getById(@RequestBody GetClientByIdRequest request) {
    return ResponseEntity.ok(service.getById(request.clientId()));
}

// Service (NO CHANGE - signature compatible)
public ClientResponse getById(Long clientId) { ... }
```

---

This template ensures:
✅ Security (no sensitive data in URL)  
✅ Consistency (English, versioning, naming)  
✅ Maintainability (JavaDoc, clear patterns)  
✅ Backward compatibility (API versioning)


