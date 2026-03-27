# 🚀 Cheat Sheet: Crear un nuevo dominio en DDD

## Copia y adapta este patrón para nuevos dominios

Cambia `MiDominio` por tu nombre (ej: `Cliente`, `Pago`, `Caso`).

---

## 1️⃣ Domain Model

**Archivo:** `src/main/java/coovitelCobranza/cobranzas/midominio/domain/model/MiDominio.java`

```java
package coovitelCobranza.cobranzas.midominio.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class MiDominio {

    private final Long id;
    private final Long parentId;  // Si pertenece a otro dominio
    private String nombre;
    private EstadoMiDominio estado;
    private LocalDateTime updatedAt;

    private MiDominio(Long id, Long parentId, String nombre, EstadoMiDominio estado) {
        this.id = id;
        this.parentId = Objects.requireNonNull(parentId, "parentId requerido");
        this.nombre = Objects.requireNonNull(nombre, "nombre requerido");
        this.estado = Objects.requireNonNull(estado, "estado requerido");
        this.updatedAt = LocalDateTime.now();
    }

    public static MiDominio crear(Long parentId, String nombre) {
        return new MiDominio(null, parentId, nombre, EstadoMiDominio.ACTIVO);
    }

    public static MiDominio reconstruir(Long id, Long parentId, String nombre, 
                                        EstadoMiDominio estado, LocalDateTime updatedAt) {
        MiDominio dominio = new MiDominio(id, parentId, nombre, estado);
        dominio.updatedAt = updatedAt;
        return dominio;
    }

    public void cambiarNombre(String nuevoNombre) {
        if (nuevoNombre == null || nuevoNombre.isBlank()) {
            throw new IllegalArgumentException("nombre no puede estar vacío");
        }
        this.nombre = nuevoNombre;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters (sin setters, mutación vía métodos de negocio)
    public Long getId() { return id; }
    public Long getParentId() { return parentId; }
    public String getNombre() { return nombre; }
    public EstadoMiDominio getEstado() { return estado; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public enum EstadoMiDominio {
        ACTIVO,
        INACTIVO,
        SUSPENDIDO
    }
}
```

---

## 2️⃣ Domain Repository (Interface)

**Archivo:** `src/main/java/coovitelCobranza/cobranzas/midominio/domain/repository/MiDominioRepository.java`

```java
package coovitelCobranza.cobranzas.midominio.domain.repository;

import coovitelCobranza.cobranzas.midominio.domain.model.MiDominio;

import java.util.List;
import java.util.Optional;

public interface MiDominioRepository {

    MiDominio save(MiDominio miDominio);

    Optional<MiDominio> findById(Long id);

    List<MiDominio> findByParentId(Long parentId);
}
```

---

## 3️⃣ Domain Exceptions

**Archivo:** `src/main/java/coovitelCobranza/cobranzas/midominio/domain/exception/MiDominioNotFoundException.java`

```java
package coovitelCobranza.cobranzas.midominio.domain.exception;

public class MiDominioNotFoundException extends RuntimeException {
    public MiDominioNotFoundException(Long id) {
        super("No se encontro MiDominio con id: " + id);
    }
}
```

**Archivo:** `src/main/java/coovitelCobranza/cobranzas/midominio/domain/exception/MiDominioBusinessException.java`

```java
package coovitelCobranza.cobranzas.midominio.domain.exception;

public class MiDominioBusinessException extends RuntimeException {
    public MiDominioBusinessException(String message) {
        super(message);
    }
}
```

---

## 4️⃣ DTOs

**Archivo:** `src/main/java/coovitelCobranza/cobranzas/midominio/application/dto/MiDominioResponse.java`

```java
package coovitelCobranza.cobranzas.midominio.application.dto;

import coovitelCobranza.cobranzas.midominio.domain.model.MiDominio;

import java.time.LocalDateTime;

public record MiDominioResponse(
        Long id,
        Long parentId,
        String nombre,
        String estado,
        LocalDateTime updatedAt
) {
    public static MiDominioResponse fromDomain(MiDominio miDominio) {
        return new MiDominioResponse(
                miDominio.getId(),
                miDominio.getParentId(),
                miDominio.getNombre(),
                miDominio.getEstado().name(),
                miDominio.getUpdatedAt()
        );
    }
}
```

**Archivo:** `src/main/java/coovitelCobranza/cobranzas/midominio/application/dto/CrearMiDominioRequest.java`

```java
package coovitelCobranza.cobranzas.midominio.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CrearMiDominioRequest(
        @NotNull(message = "parentId es requerido")
        Long parentId,
        @NotBlank(message = "nombre es requerido")
        String nombre
) {
}
```

---

## 5️⃣ Application Service

**Archivo:** `src/main/java/coovitelCobranza/cobranzas/midominio/application/service/MiDominioApplicationService.java`

```java
package coovitelCobranza.cobranzas.midominio.application.service;

import coovitelCobranza.cobranzas.midominio.application.dto.CrearMiDominioRequest;
import coovitelCobranza.cobranzas.midominio.application.dto.MiDominioResponse;
import coovitelCobranza.cobranzas.midominio.domain.exception.MiDominioBusinessException;
import coovitelCobranza.cobranzas.midominio.domain.exception.MiDominioNotFoundException;
import coovitelCobranza.cobranzas.midominio.domain.model.MiDominio;
import coovitelCobranza.cobranzas.midominio.domain.repository.MiDominioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MiDominioApplicationService {

    private final MiDominioRepository repository;

    public MiDominioApplicationService(MiDominioRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public MiDominioResponse crear(CrearMiDominioRequest request) {
        MiDominio miDominio = MiDominio.crear(request.parentId(), request.nombre());
        MiDominio saved = repository.save(miDominio);
        return MiDominioResponse.fromDomain(saved);
    }

    @Transactional(readOnly = true)
    public MiDominioResponse obtenerPorId(Long id) {
        MiDominio miDominio = repository.findById(id)
                .orElseThrow(() -> new MiDominioNotFoundException(id));
        return MiDominioResponse.fromDomain(miDominio);
    }

    @Transactional(readOnly = true)
    public List<MiDominioResponse> listarPorParent(Long parentId) {
        return repository.findByParentId(parentId).stream()
                .map(MiDominioResponse::fromDomain)
                .toList();
    }

    @Transactional
    public MiDominioResponse cambiarNombre(Long id, String nuevoNombre) {
        MiDominio miDominio = repository.findById(id)
                .orElseThrow(() -> new MiDominioNotFoundException(id));

        try {
            miDominio.cambiarNombre(nuevoNombre);
        } catch (IllegalArgumentException e) {
            throw new MiDominioBusinessException(e.getMessage());
        }

        MiDominio saved = repository.save(miDominio);
        return MiDominioResponse.fromDomain(saved);
    }
}
```

---

## 6️⃣ JPA Entity

**Archivo:** `src/main/java/coovitelCobranza/cobranzas/midominio/infrastructure/persistence/MiDominioJpaEntity.java`

```java
package coovitelCobranza.cobranzas.midominio.infrastructure.persistence;

import coovitelCobranza.cobranzas.midominio.domain.model.MiDominio;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "mi_dominio")  // Cambia al nombre de tu tabla
public class MiDominioJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "estado")
    private Integer estado;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public MiDominio toDomain() {
        return MiDominio.reconstruir(
                id,
                parentId,
                nombre,
                mapEstado(estado),
                updatedAt
        );
    }

    public static MiDominioJpaEntity fromDomain(MiDominio miDominio) {
        MiDominioJpaEntity entity = new MiDominioJpaEntity();
        entity.id = miDominio.getId();
        entity.parentId = miDominio.getParentId();
        entity.nombre = miDominio.getNombre();
        entity.estado = mapEstado(miDominio.getEstado());
        entity.updatedAt = miDominio.getUpdatedAt();
        return entity;
    }

    private static MiDominio.EstadoMiDominio mapEstado(Integer estado) {
        if (estado == null) return MiDominio.EstadoMiDominio.ACTIVO;
        return switch (estado) {
            case 2 -> MiDominio.EstadoMiDominio.INACTIVO;
            case 3 -> MiDominio.EstadoMiDominio.SUSPENDIDO;
            default -> MiDominio.EstadoMiDominio.ACTIVO;
        };
    }

    private static Integer mapEstado(MiDominio.EstadoMiDominio estado) {
        return switch (estado) {
            case INACTIVO -> 2;
            case SUSPENDIDO -> 3;
            default -> 1;
        };
    }
}
```

---

## 7️⃣ JPA Repository (Spring Data)

**Archivo:** `src/main/java/coovitelCobranza/cobranzas/midominio/infrastructure/persistence/MiDominioJpaRepository.java`

```java
package coovitelCobranza.cobranzas.midominio.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MiDominioJpaRepository extends JpaRepository<MiDominioJpaEntity, Long> {
    List<MiDominioJpaEntity> findByParentId(Long parentId);
}
```

---

## 8️⃣ Repository Adapter

**Archivo:** `src/main/java/coovitelCobranza/cobranzas/midominio/infrastructure/persistence/MiDominioRepositoryImpl.java`

```java
package coovitelCobranza.cobranzas.midominio.infrastructure.persistence;

import coovitelCobranza.cobranzas.midominio.domain.model.MiDominio;
import coovitelCobranza.cobranzas.midominio.domain.repository.MiDominioRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MiDominioRepositoryImpl implements MiDominioRepository {

    private final MiDominioJpaRepository jpaRepository;

    public MiDominioRepositoryImpl(MiDominioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public MiDominio save(MiDominio miDominio) {
        MiDominioJpaEntity entity = MiDominioJpaEntity.fromDomain(miDominio);
        MiDominioJpaEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<MiDominio> findById(Long id) {
        return jpaRepository.findById(id).map(MiDominioJpaEntity::toDomain);
    }

    @Override
    public List<MiDominio> findByParentId(Long parentId) {
        return jpaRepository.findByParentId(parentId).stream()
                .map(MiDominioJpaEntity::toDomain)
                .toList();
    }
}
```

---

## 9️⃣ Controller REST

**Archivo:** `src/main/java/coovitelCobranza/cobranzas/midominio/infrastructure/web/MiDominioController.java`

```java
package coovitelCobranza.cobranzas.midominio.infrastructure.web;

import coovitelCobranza.cobranzas.midominio.application.dto.CrearMiDominioRequest;
import coovitelCobranza.cobranzas.midominio.application.dto.MiDominioResponse;
import coovitelCobranza.cobranzas.midominio.application.service.MiDominioApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mi-dominio")
@Tag(name = "Mi Dominio", description = "API de MiDominio")
public class MiDominioController {

    private final MiDominioApplicationService service;

    public MiDominioController(MiDominioApplicationService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear nuevo MiDominio")
    public ResponseEntity<MiDominioResponse> crear(@Valid @RequestBody CrearMiDominioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener MiDominio por id")
    public ResponseEntity<MiDominioResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @GetMapping("/parent/{parentId}")
    @Operation(summary = "Listar por parent")
    public ResponseEntity<List<MiDominioResponse>> listarPorParent(@PathVariable Long parentId) {
        return ResponseEntity.ok(service.listarPorParent(parentId));
    }

    @PutMapping("/{id}/nombre")
    @Operation(summary = "Cambiar nombre")
    public ResponseEntity<MiDominioResponse> cambiarNombre(@PathVariable Long id,
                                                            @RequestParam String nuevoNombre) {
        return ResponseEntity.ok(service.cambiarNombre(id, nuevoNombre));
    }
}
```

---

## 🔟 Exception Handler

**Archivo:** `src/main/java/coovitelCobranza/cobranzas/midominio/infrastructure/web/MiDominioExceptionHandler.java`

```java
package coovitelCobranza.cobranzas.midominio.infrastructure.web;

import coovitelCobranza.cobranzas.midominio.domain.exception.MiDominioBusinessException;
import coovitelCobranza.cobranzas.midominio.domain.exception.MiDominioNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice(basePackages = "coovitelCobranza.cobranzas")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MiDominioExceptionHandler {

    @ExceptionHandler(MiDominioNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(MiDominioNotFoundException exception) {
        return buildResponse(HttpStatus.NOT_FOUND, "MIDOMINIO_NOT_FOUND", exception.getMessage());
    }

    @ExceptionHandler(MiDominioBusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusiness(MiDominioBusinessException exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, "MIDOMINIO_BUSINESS_ERROR", exception.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", status.value(),
                "code", code,
                "message", message
        ));
    }
}
```

---

## 1️⃣1️⃣ Unit Test

**Archivo:** `src/test/java/coovitelCobranza/cobranzas/midominio/domain/model/MiDominioTest.java`

```java
package coovitelCobranza.cobranzas.midominio.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MiDominioTest {

    @Test
    void deberiaCrearMiDominioConEstadoActivo() {
        MiDominio miDominio = MiDominio.crear(10L, "Nombre Test");

        assertEquals(10L, miDominio.getParentId());
        assertEquals("Nombre Test", miDominio.getNombre());
        assertEquals(MiDominio.EstadoMiDominio.ACTIVO, miDominio.getEstado());
    }

    @Test
    void deberiaLanzarErrorSiNombreVacio() {
        MiDominio miDominio = MiDominio.crear(10L, "Nombre");

        assertThrows(IllegalArgumentException.class, () -> miDominio.cambiarNombre(""));
    }
}
```

---

## ✅ Checklist: Después de copiar/pegar

- [ ] Cambié `MiDominio` por mi nombre de dominio (search & replace)
- [ ] Cambié nombre de tabla en `@Table(name = "...")`
- [ ] Cambié paquete base `midominio` a mi nombre
- [ ] Creé tabla en BD: `CREATE TABLE mi_dominio (...)`
- [ ] Compilé: `mvn clean compile`
- [ ] Ejecuté tests: `mvn test`
- [ ] Probé en Swagger: `http://localhost:8080/swagger-ui/index.html`
- [ ] Creé un recurso: `POST /api/mi-dominio`
- [ ] Consulté: `GET /api/mi-dominio/1`

---

## 📋 Notas finales

1. **Nombres:** Usa nombres singulares para dominios (`Cliente`, no `Clientes`)
2. **IDs:** Siempre `Long id`, nunca `String` ni `Integer`
3. **Enums:** Estado debe ser enum, no String
4. **Validaciones:** Hacer en dominio (IllegalArgumentException), no en DTO
5. **Transacciones:** `@Transactional` en Application Service
6. **Conversiones:** Domain ↔ DTO en Application, Domain ↔ JpaEntity en Persistence
7. **Excepciones:** `NotFound` (404), `BusinessException` (400)


