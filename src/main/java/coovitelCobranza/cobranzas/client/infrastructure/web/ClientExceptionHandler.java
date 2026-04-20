package coovitelCobranza.cobranzas.client.infrastructure.web;

import coovitelCobranza.cobranzas.client.application.exception.ClientBusinessException;
import coovitelCobranza.cobranzas.client.application.exception.ClientNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador centralizado de excepciones para operaciones de clientes.
 * Traduce excepciones de negocio a respuestas HTTP apropiadas con detalles del error.
 */
@RestControllerAdvice
public class ClientExceptionHandler {

    /**
     * Maneja excepciones cuando no se encuentra un cliente.
     *
     * @param ex excepción lanzada cuando el cliente no existe
     * @return ResponseEntity con status 404 y detalles del error
     */
    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleClientNotFoundException(ClientNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Client not found");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Maneja excepciones cuando se viola una regla de negocio.
     *
     * @param ex excepción lanzada por violación de reglas de negocio
     * @return ResponseEntity con status 400 y detalles del error
     */
    @ExceptionHandler(ClientBusinessException.class)
    public ResponseEntity<Map<String, Object>> handleClientBusinessException(ClientBusinessException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Business rule violation");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Maneja excepciones genéricas no previstas.
     *
     * @param ex excepción genérica capturada
     * @return ResponseEntity con status 500 y detalles del error
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

