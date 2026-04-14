package coovitelCobranza.cobranzas.cargamasiva.infrastructure.web.exception;

import coovitelCobranza.cobranzas.cargamasiva.application.dto.CargaMasivaResultResponse;
import coovitelCobranza.cobranzas.cargamasiva.application.dto.RowErrorDTO;
import coovitelCobranza.cobranzas.cargamasiva.application.exception.CargaMasivaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.List;

/**
 * Manejador de excepciones para el módulo de Carga Masiva.
 *
 * <p>Tiene {@code @Order(-1)} para ejecutarse ANTES que {@code GlobalExceptionHandler}
 * ({@code @Order(0)}), garantizando que las excepciones de negocio de carga masiva
 * devuelvan {@link CargaMasivaResultResponse} y no {@code ErrorResponse}.
 *
 * <p>Aplica SOLO a {@code CargaMasivaController} gracias a {@code basePackages}.
 */
@Order(-1)
@RestControllerAdvice(basePackages =
        "coovitelCobranza.cobranzas.cargamasiva.infrastructure.web.controller")
public class CargaMasivaExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(CargaMasivaExceptionHandler.class);

    /**
     * Errores de validación de negocio → HTTP 422 Unprocessable Entity.
     * Devuelve la lista completa de errores por fila/campo.
     */
    @ExceptionHandler(CargaMasivaException.class)
    public ResponseEntity<CargaMasivaResultResponse> handleCargaMasiva(CargaMasivaException ex) {
        log.warn("[HANDLER] CargaMasivaException: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(CargaMasivaResultResponse.failed(
                        ex.getTotalRows(),
                        ex.getFileName(),
                        ex.getErrors()));
    }

    /**
     * Archivo vacío o extensión inválida → HTTP 400 Bad Request.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CargaMasivaResultResponse> handleIllegalArgument(
            IllegalArgumentException ex) {
        log.warn("[HANDLER] IllegalArgumentException: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CargaMasivaResultResponse.failed(
                        0,
                        "unknown",
                        List.of(RowErrorDTO.global(ex.getMessage()))));
    }

    /**
     * Archivo demasiado grande → HTTP 413 Payload Too Large.
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<CargaMasivaResultResponse> handleMaxUploadSize(
            MaxUploadSizeExceededException ex) {
        log.warn("[HANDLER] Archivo demasiado grande: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(CargaMasivaResultResponse.failed(
                        0,
                        "unknown",
                        List.of(RowErrorDTO.global(
                                "El archivo excede el tamaño máximo permitido (50MB)."))));
    }

    /**
     * Errores de I/O inesperados → HTTP 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CargaMasivaResultResponse> handleGeneric(Exception ex) {
        log.error("[HANDLER] Error inesperado en carga masiva", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CargaMasivaResultResponse.failed(
                        0,
                        "unknown",
                        List.of(RowErrorDTO.global(
                                "Error interno del servidor. Contacte al administrador."))));
    }
}
