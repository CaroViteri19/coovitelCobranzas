package coovitelCobranza.security.infrastructure.web;

import coovitelCobranza.security.application.exception.InvalidCredentialsException;
import coovitelCobranza.security.application.exception.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manejador de excepciones global para el módulo de seguridad.
 * Transforma excepciones personalizadas en respuestas HTTP apropiadas.
 */
@RestControllerAdvice(basePackages = "coovitelCobranza.security")
public class AuthExceptionHandler {

    /**
     * Maneja excepciones de credenciales inválidas.
     *
     * @param ex Excepción de credenciales inválidas.
     * @return Respuesta con estado 401 (Unauthorized).
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(InvalidCredentialsException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "Unauthorized");
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    /**
     * Maneja excepciones cuando un usuario ya existe.
     *
     * @param ex Excepción de usuario ya existente.
     * @return Respuesta con estado 409 (Conflict).
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "Conflict");
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.CONFLICT.value());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Maneja excepciones de validación de argumentos.
     *
     * @param ex Excepción de validación no exitosa.
     * @return Respuesta con estado 400 (Bad Request).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "Bad request");
        body.put("message", ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
                .orElse("Validation error"));
        body.put("status", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}

