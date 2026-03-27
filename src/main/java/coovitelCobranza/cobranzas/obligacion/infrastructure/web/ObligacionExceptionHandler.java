package coovitelCobranza.cobranzas.obligacion.infrastructure.web;

import coovitelCobranza.cobranzas.obligacion.application.exception.ObligacionBusinessException;
import coovitelCobranza.cobranzas.obligacion.application.exception.ObligacionNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "coovitelCobranza.cobranzas")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ObligacionExceptionHandler {

    @ExceptionHandler(ObligacionNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ObligacionNotFoundException exception) {
        return buildResponse(HttpStatus.NOT_FOUND, "OBLIGACION_NOT_FOUND", exception.getMessage());
    }

    @ExceptionHandler(ObligacionBusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusiness(ObligacionBusinessException exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, "OBLIGACION_BUSINESS_ERROR", exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException exception) {
        String message = "Invalid request";
        FieldError fieldError = exception.getBindingResult().getFieldError();
        if (fieldError != null && fieldError.getDefaultMessage() != null) {
            message = fieldError.getDefaultMessage();
        }
        return buildResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message);
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String code, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("code", code);
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}

