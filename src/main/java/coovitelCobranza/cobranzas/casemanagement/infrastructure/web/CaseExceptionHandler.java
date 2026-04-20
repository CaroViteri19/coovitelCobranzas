package coovitelCobranza.cobranzas.casemanagement.infrastructure.web;

import coovitelCobranza.cobranzas.casemanagement.application.exception.CaseBusinessException;
import coovitelCobranza.cobranzas.casemanagement.application.exception.CaseNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para operaciones de casos de cobranza.
 *
 * Captura excepciones específicas del dominio de casos y las convierte
 * en respuestas HTTP con información de error adecuada.
 */
@RestControllerAdvice
public class CaseExceptionHandler {

    /**
     * Maneja excepciones cuando un caso no es encontrado.
     *
     * @param ex la excepción de caso no encontrado
     * @return ResponseEntity con status 404 y mensaje de error
     */
    @ExceptionHandler(CaseNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCaseNotFoundException(CaseNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Case not found");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Maneja excepciones cuando se viola una regla de negocio.
     *
     * @param ex la excepción de negocio
     * @return ResponseEntity con status 400 y mensaje de error
     */
    @ExceptionHandler(CaseBusinessException.class)
    public ResponseEntity<Map<String, Object>> handleCaseBusinessException(CaseBusinessException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Business rule violation");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Maneja cualquier excepción no controlada que ocurra durante la operación.
     *
     * @param ex la excepción genérica
     * @return ResponseEntity con status 500 y mensaje de error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal server error");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}

