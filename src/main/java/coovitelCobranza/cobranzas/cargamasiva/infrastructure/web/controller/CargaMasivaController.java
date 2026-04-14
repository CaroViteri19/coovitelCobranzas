package coovitelCobranza.cobranzas.cargamasiva.infrastructure.web.controller;

import coovitelCobranza.cobranzas.cargamasiva.application.dto.CargaMasivaResultResponse;
import coovitelCobranza.cobranzas.cargamasiva.application.service.CargaMasivaApplicationService;
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
 *   <li>Acepta archivos CSV via {@code multipart/form-data}</li>
 *   <li>Requiere rol ADMINISTRADOR o SUPERVISOR</li>
 *   <li>Retorna HTTP 201 en éxito o HTTP 422 en caso de errores de validación</li>
 * </ul>
 *
 * <p>El manejo de errores de validación se delega a
 * {@link CargaMasivaExceptionHandler} via {@code @RestControllerAdvice}.
 */
@RestController
@RequestMapping("/api/v1/carga-masiva")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CargaMasivaController {

    private final CargaMasivaApplicationService service;

    public CargaMasivaController(CargaMasivaApplicationService service) {
        this.service = service;
    }

    /**
     * Recibe un archivo CSV y ejecuta el proceso de carga masiva.
     *
     * <p>Retorna HTTP 201 con estadísticas si todo fue exitoso.
     * Retorna HTTP 422 con lista detallada de errores si la validación falla.
     *
     * @param file  archivo CSV recibido como multipart
     * @param jwt   token JWT del usuario autenticado (para auditoría)
     * @return resultado de la carga
     */
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPERVISOR')")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CargaMasivaResultResponse> upload(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal Jwt jwt) {

        validateFileNotEmpty(file);
        validateFileExtension(file);

        String uploadedBy = jwt != null ? jwt.getSubject() : "anonymous";
        CargaMasivaResultResponse result = service.procesarCarga(file, uploadedBy);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // ── Validaciones básicas del archivo ──────────────────────────────────────

    private void validateFileNotEmpty(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío.");
        }
    }

    private void validateFileExtension(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (name == null || !name.toLowerCase().endsWith(".csv")) {
            throw new IllegalArgumentException(
                    "Solo se aceptan archivos con extensión .csv. Archivo recibido: " + name);
        }
    }
}
