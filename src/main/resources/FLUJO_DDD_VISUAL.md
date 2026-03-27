# Conexión DDD: De la solicitud HTTP a la base de datos

## Escenario real: Un usuario quiere pagar su obligación

```
┌─────────────────────────────────────────────────────────────────────┐
│ CLIENTE (Postman, navegador, app móvil)                             │
└────────────────────────┬────────────────────────────────────────────┘
                         │
                         │ HTTP PUT /api/obligaciones/5/pago
                         │ Content-Type: application/json
                         │ Authorization: Bearer eyJhbGc...
                         │
                         │ { "valorPago": 50000.00 }
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────────┐
│ SPRING SECURITY + SWAGGER DOCUMENTATION                             │
│ - Valida que usuario esté autenticado (JWT)                         │
│ - Documentación visible en /swagger-ui/index.html                   │
└────────────────────────┬────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────────┐
│ LAYER: WEB (Infrastructure)                                         │
│ ─────────────────────────────────────────────────────────────────── │
│ Archivo: ObligacionController.java                                  │
│                                                                     │
│ @PutMapping("/{id}/pago")                                           │
│ public ResponseEntity<ObligacionResponse> aplicarPago(              │
│     @PathVariable Long id,                                          │
│     @Valid @RequestBody AplicarPagoRequest request)                 │
│                                                                     │
│ 1. Spring valida que request tenga "valorPago" válido              │
│    Si falla → ObligacionExceptionHandler captura                   │
│    Retorna 400 + código "VALIDATION_ERROR"                         │
│                                                                     │
│ 2. Si OK → llama a application service                             │
│    obligacionApplicationService.aplicarPago(5, 50000.00)           │
└────────────────────────┬────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────────┐
│ LAYER: APPLICATION (Use Case Orchestration)                         │
│ ─────────────────────────────────────────────────────────────────── │
│ Archivo: ObligacionApplicationService.java                          │
│                                                                     │
│ @Transactional                                                      │
│ public ObligacionResponse aplicarPago(Long id, BigDecimal valor) {  │
│                                                                     │
│   1. Obtiene la obligación del dominio:                            │
│      Obligacion obligacion = repository.findById(5)                │
│      .orElseThrow(() -> new ObligacionNotFoundException(5))        │
│                                                                     │
│      Si no existe → ObligacionExceptionHandler captura             │
│      Retorna 404 + código "OBLIGACION_NOT_FOUND"                   │
│                                                                     │
│   2. Ejecuta lógica de negocio en el modelo:                       │
│      obligacion.aplicarPago(50000.00)                              │
│                                                                     │
│   3. Persiste cambios:                                             │
│      Obligacion saved = repository.save(obligacion)                │
│                                                                     │
│   4. Convierte a DTO para HTTP:                                    │
│      return ObligacionResponse.fromDomain(saved)                   │
│ }                                                                   │
│                                                                     │
│ NOTAS:                                                              │
│ - @Transactional asegura atomicidad (todo o nada)                 │
│ - Si algún paso falla, se revierte la transacción                 │
└────────────────────────┬────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────────┐
│ LAYER: DOMAIN (Pure Business Logic)                                 │
│ ─────────────────────────────────────────────────────────────────── │
│ Archivo: Obligacion.java                                            │
│                                                                     │
│ public void aplicarPago(BigDecimal valorPago) {                     │
│                                                                     │
│     // Validación: no permitir pagos inválidos                     │
│     if (valorPago == null || valorPago.signum() <= 0) {            │
│         throw new IllegalArgumentException(                        │
│             "valorPago debe ser mayor a cero"                      │
│         );                                                          │
│         // Si llega aquí → ObligacionExceptionHandler captura      │
│         // Retorna 400 + código "OBLIGACION_BUSINESS_ERROR"       │
│     }                                                               │
│                                                                     │
│     // Lógica de negocio pura                                      │
│     this.saldoTotal = this.saldoTotal.subtract(valorPago)          │
│                                         .max(BigDecimal.ZERO);     │
│     this.saldoVencido = this.saldoVencido.subtract(valorPago)      │
│                                           .max(BigDecimal.ZERO);   │
│                                                                     │
│     // Si quedó sin deuda → marcar como CANCELADA                 │
│     if (this.saldoTotal.signum() == 0) {                           │
│         this.estado = EstadoObligacion.CANCELADA;                  │
│         this.diasMora = 0;                                         │
│     }                                                               │
│                                                                     │
│     // Auditoría: registrar cuándo cambió                          │
│     this.updatedAt = LocalDateTime.now();                          │
│ }                                                                   │
│                                                                     │
│ CARACTERÍSTICAS:                                                    │
│ ✅ Sin Spring, sin JPA, sin SQL                                    │
│ ✅ Fácil de testear (solo cálculos)                                │
│ ✅ Fácil de entender (lógica de negocio pura)                      │
│ ✅ Fácil de cambiar (sin acoplamiento)                             │
└────────────────────────┬────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────────┐
│ LAYER: PERSISTENCE (Database Adapter)                               │
│ ─────────────────────────────────────────────────────────────────── │
│ Archivo: ObligacionRepositoryImpl.java                               │
│                                                                     │
│ @Repository                                                         │
│ public class ObligacionRepositoryImpl                               │
│     implements ObligacionRepository {  // ← Contrato del dominio   │
│                                                                     │
│   private final ObligacionJpaRepository jpaRepository;             │
│                                                                     │
│   @Override                                                         │
│   public Obligacion save(Obligacion obligacion) {                  │
│                                                                     │
│       // 1. Convertir dominio → persistencia                       │
│       ObligacionJpaEntity jpaEntity =                              │
│           ObligacionJpaEntity.fromDomain(obligacion);              │
│       // obligacion.saldoTotal → jpaEntity.totalBalance            │
│       // obligacion.estado (ENUM) → jpaEntity.status (INT)         │
│                                                                     │
│       // 2. Guardar en JPA                                         │
│       ObligacionJpaEntity saved = jpaRepository.save(jpaEntity);   │
│       // ← Spring Data ejecuta INSERT/UPDATE                       │
│                                                                     │
│       // 3. Convertir persistencia → dominio                       │
│       return saved.toDomain();                                     │
│       // jpaEntity.totalBalance → obligacion.saldoTotal            │
│       // jpaEntity.status (INT) → obligacion.estado (ENUM)         │
│   }                                                                 │
│ }                                                                   │
│                                                                     │
│ NOTAS:                                                              │
│ - La interfaz ObligacionRepository NO tiene anotaciones Spring     │
│ - La implementación ObligacionRepositoryImpl sí tiene @Repository   │
│ - El dominio nunca ve JPA ni SQL                                   │
└────────────────────────┬────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────────┐
│ LAYER: JPA (Spring Data)                                            │
│ ─────────────────────────────────────────────────────────────────── │
│ Archivo: ObligacionJpaRepository.java                               │
│                                                                     │
│ public interface ObligacionJpaRepository                            │
│     extends JpaRepository<ObligacionJpaEntity, Long> {             │
│                                                                     │
│     Optional<ObligacionJpaEntity> findByObligationNumber(...)      │
│     List<ObligacionJpaEntity> findByCustomerId(...)               │
│ }                                                                   │
│                                                                     │
│ Spring genera automáticamente:                                     │
│ - INSERT: INSERT INTO obligation (...)                             │
│ - SELECT: SELECT * FROM obligation WHERE id = ?                   │
│ - UPDATE: UPDATE obligation SET total_balance = ? WHERE id = ?    │
│ - DELETE: DELETE FROM obligation WHERE id = ?                     │
└────────────────────────┬────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────────┐
│ JPA ENTITY (Mapper DB ↔ Java)                                       │
│ ─────────────────────────────────────────────────────────────────── │
│ Archivo: ObligacionJpaEntity.java                                   │
│                                                                     │
│ @Entity                                                             │
│ @Table(name = "obligation")                                        │
│ public class ObligacionJpaEntity {                                  │
│                                                                     │
│     @Id                                                             │
│     @Column(name = "id")                                            │
│     private Long id;                                                │
│                                                                     │
│     @Column(name = "total_balance")                                 │
│     private BigDecimal totalBalance;  ← Mapea a BD                 │
│                                                                     │
│     @Column(name = "status")                                        │
│     private Integer status;  ← 1=AL_DIA, 2=EN_MORA, 4=CANCELADA   │
│                                                                     │
│     public Obligacion toDomain() { /* conversión */ }              │
│     public static ObligacionJpaEntity fromDomain(...) { /* ... */ } │
│ }                                                                   │
│                                                                     │
│ NOTAS:                                                              │
│ - Tiene @Entity, @Table, @Column (solo aquí)                      │
│ - Convierte enums a integers, BigDecimal a decimal, etc.          │
│ - El dominio Obligacion NO tiene estas anotaciones                 │
└────────────────────────┬────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────────┐
│ DATABASE (MySQL)                                                    │
│ ─────────────────────────────────────────────────────────────────── │
│ Table: obligation                                                   │
│                                                                     │
│ id  customer_id obligation_number total_balance overdue_balance    │
│ ─────────────────────────────────────────────────────────────────  │
│  5        10       OBL-001        100000.00       50000.00         │
│                                                                     │
│ UPDATE obligation                                                   │
│    SET total_balance = 50000.00,        ← Pagó 50k                │
│        updated_at = NOW()               ← Auditoría               │
│  WHERE id = 5                                                       │
│                                                                     │
│ Resultado después de pago:                                         │
│  5        10       OBL-001         50000.00       0.00             │
│                            ↑                      ↑                │
│                       Actualizado            No hay más            │
│                                            saldo vencido           │
└────────────────────────┬────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────────┐
│ RESPUESTA HTTP                                                      │
│ ─────────────────────────────────────────────────────────────────── │
│ Status: 200 OK                                                      │
│ Content-Type: application/json                                      │
│                                                                     │
│ {                                                                   │
│   "id": 5,                                                          │
│   "clienteId": 10,                                                  │
│   "numeroObligacion": "OBL-001",                                    │
│   "saldoTotal": 50000.00,          ← Actualizado                  │
│   "saldoVencido": 0.00,            ← Actualizado                  │
│   "diasMora": 15,                                                   │
│   "estado": "EN_MORA",             ← Aún en mora (otros vencidos) │
│   "fechaVencimiento": "2026-04-30",                                │
│   "updatedAt": "2026-03-27T11:30:00"  ← Timestamp del pago       │
│ }                                                                   │
│                                                                     │
│ Si hubo ERROR:                                                      │
│ Status: 400 Bad Request                                             │
│ {                                                                   │
│   "timestamp": "2026-03-27T11:31:00.123Z",                         │
│   "status": 400,                                                    │
│   "code": "OBLIGACION_BUSINESS_ERROR",                              │
│   "message": "valorPago debe ser mayor a cero"                     │
│ }                                                                   │
└────────────────────────┬────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────────┐
│ CLIENTE recibe respuesta                                            │
│ - Si OK: actualiza interfaz gráfica, muestra confirmación          │
│ - Si error: muestra mensaje de error amigable                      │
└─────────────────────────────────────────────────────────────────────┘
```

---

## Resumen por capas

| Capa | Archivo | Responsabilidad | Spring | Testeable |
|------|---------|-----------------|--------|-----------|
| **Web** | `ObligacionController` | HTTP ↔ DTO | ✅ | Con MockMvc |
| **Web** | `ObligacionExceptionHandler` | Errores HTTP | ✅ | Con MockMvc |
| **Application** | `ObligacionApplicationService` | Orquestación de casos de uso | ✅ | Con mocks |
| **Domain** | `Obligacion` | Lógica de negocio pura | ❌ | ✅ (sin mocks) |
| **Domain** | `ObligacionRepository` (interface) | Contrato de persistencia | ❌ | N/A (interfaz) |
| **Persistence** | `ObligacionRepositoryImpl` | Implementación del repositorio | ✅ | Con H2 |
| **Persistence** | `ObligacionJpaRepository` | Spring Data CRUD | ✅ | Con H2 |
| **Persistence** | `ObligacionJpaEntity` | Mapeo BD ↔ Java | ✅ | Rara vez |

---

## Flujo de excepciones (errorhandling)

```
┌──────────────────────────────────────┐
│ HTTP REQUEST                         │
└─────────────┬────────────────────────┘
              │
        try {
              │
    ┌─────────▼──────────────────┐
    │ oblig.aplicarPago(negativo) │
    └─────────┬──────────────────┘
              │
        throw IllegalArgumentException
              │
        } catch (?) {
              │
    ┌─────────▼──────────────────────────────────────┐
    │ ObligacionApplicationService.aplicarPago(...)  │
    │ catch (IllegalArgumentException e) {           │
    │     throw new                                   │
    │     ObligacionBusinessException(e.getMessage()) │
    │ }                                               │
    └─────────┬──────────────────────────────────────┘
              │
        throw ObligacionBusinessException
              │
    ┌─────────▼──────────────────────────────┐
    │ Spring MVC (DispatcherServlet)         │
    │ Busca @ExceptionHandler para esta tipo │
    └─────────┬──────────────────────────────┘
              │
    ┌─────────▼────────────────────────────────────────┐
    │ ObligacionExceptionHandler.handleBusiness(exc)   │
    │ @ExceptionHandler(ObligacionBusinessException)   │
    │                                                  │
    │ return ResponseEntity                           │
    │   .status(400)                                   │
    │   .body(Map.of(                                 │
    │     "timestamp": "2026-03-27T...",              │
    │     "status": 400,                              │
    │     "code": "OBLIGACION_BUSINESS_ERROR",        │
    │     "message": "valorPago debe ser > 0"         │
    │   ))                                            │
    └─────────┬────────────────────────────────────────┘
              │
┌─────────────▼────────────────────┐
│ HTTP 400 Bad Request              │
│ { "timestamp": ..., "code": ... } │
└──────────────────────────────────┘
```

---

## ¿Por qué esta arquitectura?

| Problema | Solución DDD |
|----------|-------------|
| **Lógica esparcida en controllers** | Centralizada en `Obligacion` (domain model) |
| **Dominio acoplado a Spring/JPA** | Dominio puro sin dependencias |
| **Difícil de testear** | Tests unitarios sin levantar BD |
| **Cambios en BD rompen lógica** | JPA abstracto detrás de repositorio |
| **Reglas inconsistentes** | Modelo asegura invariantes (estado solo cambia bien) |
| **Errores silenciosos** | Excepciones explícitas de dominio |


