package coovitelCobranza.cobranzas.bulkimport.infrastructure.web.controller;

import coovitelCobranza.cobranzas.bulkimport.application.dto.BulkImportResultResponse;
import coovitelCobranza.cobranzas.bulkimport.application.service.BulkImportApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller REST para el endpoint de carga masiva de asociados.
 *
 * <p>Expone un único endpoint {@code POST /api/v1/carga-masiva/upload} que:
 * <ul>
 *   <li>Acepta archivos {@code .csv} y {@code .txt} via {@code multipart/form-data}</li>
 *   <li>Requiere rol ADMINISTRADOR o SUPERVISOR</li>
 *   <li>Retorna HTTP 201 en éxito o HTTP 422 en caso de errores de validación</li>
 * </ul>
 *
 * <p>El manejo de errores de validación se delega a
 * {@code BulkImportExceptionHandler} via {@code @RestControllerAdvice}.
 */
@RestController
@RequestMapping("/api/v1/carga-masiva")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BulkImportController {

    private final BulkImportApplicationService service;

    public BulkImportController(BulkImportApplicationService service) {
        this.service = service;
    }

    /**
     * Recibe un archivo CSV o TXT y ejecuta el proceso de carga masiva.
     *
     * <p>Retorna HTTP 201 con estadísticas si todo fue exitoso.
     * Retorna HTTP 422 con lista detallada de errores si la validación falla.
     *
     * @param file archivo CSV o TXT recibido como multipart
     * @param jwt  token JWT del usuario autenticado (para auditoría)
     * @return resultado de la carga
     */
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SUPERVISOR')")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BulkImportResultResponse> upload(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal Jwt jwt) {

        validateFileNotEmpty(file);
        validateFileExtension(file);

        // El correlationId se genera dentro del service a través de AuditContext,
        // de modo que todos los eventos de auditoría del flujo lo hereden automáticamente.
        String uploadedBy = jwt != null ? jwt.getSubject() : "anonymous";
        BulkImportResultResponse result = service.procesarCarga(file, uploadedBy);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // ── Validaciones básicas del archivo ──────────────────────────────────────

    private void validateFileNotEmpty(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío.");
        }
    }

    /**
     * Valida que el archivo tenga extensión {@code .csv} o {@code .txt}.
     *
     * @throws IllegalArgumentException si la extensión no es soportada
     */
    private void validateFileExtension(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (name == null) {
            throw new IllegalArgumentException("El nombre del archivo no puede ser nulo.");
        }
        String lower = name.toLowerCase();
        if (!lower.endsWith(".csv") && !lower.endsWith(".txt")) {
            throw new IllegalArgumentException(
                    "Formato de archivo no soportado: '%s'. Solo se aceptan .csv y .txt.".formatted(name));
        }
    }
}
