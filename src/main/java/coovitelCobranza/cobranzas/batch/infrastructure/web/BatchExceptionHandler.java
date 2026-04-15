package coovitelCobranza.cobranzas.batch.infrastructure.web;

import coovitelCobranza.cobranzas.batch.application.dto.BatchErrorDetail;
import coovitelCobranza.cobranzas.batch.application.dto.BatchResultResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.List;

/**
 * Manejador global de excepciones para el módulo batch.
 * Formatea todos los errores bajo la misma estructura BatchResultResponse
 * para consistencia con el cliente.
 */
@RestControllerAdvice(assignableTypes = BatchController.class)
public class BatchExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(BatchExceptionHandler.class);

    /**
     * Archivo vacío, extensión no soportada u otros errores de negocio.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BatchResultResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Error de validación en carga batch: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse(ex.getMessage()));
    }

    /**
     * Archivo demasiado grande (supera spring.servlet.multipart.max-file-size).
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<BatchResultResponse> handleMaxUploadSize(MaxUploadSizeExceededException ex) {
        log.warn("Archivo excede el tamaño máximo permitido.");
        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(errorResponse("El archivo supera el tamaño máximo permitido."));
    }

    /**
     * Cualquier otro error no controlado.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BatchResultResponse> handleGeneral(Exception ex) {
        log.error("Error inesperado en carga batch: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse("Error interno del servidor: " + ex.getMessage()));
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private BatchResultResponse errorResponse(String mensaje) {
        return new BatchResultResponse(0, 0, 0,
                List.of(new BatchErrorDetail(0, mensaje)));
    }
}
