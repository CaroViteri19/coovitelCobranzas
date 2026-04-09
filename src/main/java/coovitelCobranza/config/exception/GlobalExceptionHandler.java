package coovitelCobranza.config.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.nio.file.AccessDeniedException;
import java.util.List;

/**
 * Manejador global de excepciones para toda la aplicacion.
 * Captura excepciones comunes de Spring y las excepciones base del proyecto,
 * retornando respuestas estandarizadas con {@link ErrorResponse}.
 */
@RestControllerAdvice
@Order(0)
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ========================
    // Excepciones de negocio
    // ========================

    /**
     * Maneja excepciones de recurso no encontrado (HTTP 404).
     *
     * @param ex excepción ResourceNotFoundException lanzada
     * @param request objeto HttpServletRequest de la petición actual
     * @return ResponseEntity con ErrorResponse y estado NOT_FOUND (404)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex,
                                                                 HttpServletRequest request) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Maneja excepciones de violación de reglas de negocio (HTTP 400).
     *
     * @param ex excepción BusinessException lanzada
     * @param request objeto HttpServletRequest de la petición actual
     * @return ResponseEntity con ErrorResponse y estado BAD_REQUEST (400)
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex,
                                                                  HttpServletRequest request) {
        log.warn("Violacion de regla de negocio: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Business Rule Violation",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Maneja excepciones de peticiones malformadas (HTTP 400).
     *
     * @param ex excepción BadRequestException lanzada
     * @param request objeto HttpServletRequest de la petición actual
     * @return ResponseEntity con ErrorResponse y estado BAD_REQUEST (400)
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex,
                                                           HttpServletRequest request) {
        log.warn("Peticion invalida: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Maneja excepciones de acceso no autorizado (HTTP 401).
     *
     * @param ex excepción UnauthorizedException lanzada
     * @param request objeto HttpServletRequest de la petición actual
     * @return ResponseEntity con ErrorResponse y estado UNAUTHORIZED (401)
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex,
                                                             HttpServletRequest request) {
        log.warn("Acceso no autorizado: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Maneja excepciones de acceso prohibido (HTTP 403).
     *
     * @param ex excepción ForbiddenException lanzada
     * @param request objeto HttpServletRequest de la petición actual
     * @return ResponseEntity con ErrorResponse y estado FORBIDDEN (403)
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException ex,
                                                          HttpServletRequest request) {
        log.warn("Acceso prohibido: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * Maneja excepciones de conflicto (HTTP 409).
     *
     * @param ex excepción ConflictException lanzada
     * @param request objeto HttpServletRequest de la petición actual
     * @return ResponseEntity con ErrorResponse y estado CONFLICT (409)
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex,
                                                         HttpServletRequest request) {
        log.warn("Conflicto: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Conflict",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // ========================
    // Excepciones de validacion
    // ========================

    /**
     * Maneja excepciones de validación de argumentos (HTTP 400).
     *
     * @param ex excepción MethodArgumentNotValidException lanzada
     * @param request objeto HttpServletRequest de la petición actual
     * @return ResponseEntity con ErrorResponse incluyendo detalles de errores de validación
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                           HttpServletRequest request) {
        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ErrorResponse.FieldError(error.getField(), error.getDefaultMessage()))
                .toList();

        log.warn("Error de validacion en {} campo(s)", fieldErrors.size());
        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                "La validacion fallo para uno o mas campos",
                request.getRequestURI(),
                fieldErrors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Maneja excepciones de parámetros requeridos faltantes (HTTP 400).
     *
     * @param ex excepción MissingServletRequestParameterException lanzada
     * @param request objeto HttpServletRequest de la petición actual
     * @return ResponseEntity con ErrorResponse indicando el parámetro faltante
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex,
                                                             HttpServletRequest request) {
        log.warn("Parametro requerido faltante: {}", ex.getParameterName());
        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                String.format("El parametro '%s' es requerido", ex.getParameterName()),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Maneja excepciones de desajuste de tipos de argumentos (HTTP 400).
     *
     * @param ex excepción MethodArgumentTypeMismatchException lanzada
     * @param request objeto HttpServletRequest de la petición actual
     * @return ResponseEntity con ErrorResponse indicando el tipo esperado
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                             HttpServletRequest request) {
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "desconocido";
        log.warn("Tipo de argumento invalido para '{}': se esperaba {}", ex.getName(), requiredType);
        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                String.format("El parametro '%s' debe ser de tipo '%s'", ex.getName(), requiredType),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ========================
    // Excepciones HTTP / Spring
    // ========================

    /**
     * Maneja excepciones de cuerpo de petición no legible (HTTP 400).
     *
     * @param ex excepción HttpMessageNotReadableException lanzada
     * @param request objeto HttpServletRequest de la petición actual
     * @return ResponseEntity con ErrorResponse indicando que el cuerpo es inválido
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex,
                                                            HttpServletRequest request) {
        log.warn("Cuerpo de la peticion no legible: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "El cuerpo de la peticion es invalido o no se puede leer",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Maneja excepciones de método HTTP no soportado (HTTP 405).
     *
     * @param ex excepción HttpRequestMethodNotSupportedException lanzada
     * @param request objeto HttpServletRequest de la petición actual
     * @return ResponseEntity con ErrorResponse y estado METHOD_NOT_ALLOWED (405)
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                   HttpServletRequest request) {
        log.warn("Metodo HTTP no soportado: {} en {}", ex.getMethod(), request.getRequestURI());
        ErrorResponse response = new ErrorResponse(
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                "Method Not Allowed",
                String.format("El metodo '%s' no esta soportado para este endpoint", ex.getMethod()),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    /**
     * Maneja excepciones de tipo de contenido no soportado (HTTP 415).
     *
     * @param ex excepción HttpMediaTypeNotSupportedException lanzada
     * @param request objeto HttpServletRequest de la petición actual
     * @return ResponseEntity con ErrorResponse y estado UNSUPPORTED_MEDIA_TYPE (415)
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
                                                                      HttpServletRequest request) {
        log.warn("Media type no soportado: {}", ex.getContentType());
        ErrorResponse response = new ErrorResponse(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                "Unsupported Media Type",
                String.format("El tipo de contenido '%s' no esta soportado", ex.getContentType()),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(response);
    }

    /**
     * Maneja excepciones de endpoint no encontrado (HTTP 404).
     *
     * @param ex excepción NoResourceFoundException lanzada
     * @param request objeto HttpServletRequest de la petición actual
     * @return ResponseEntity con ErrorResponse y estado NOT_FOUND (404)
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException ex,
                                                                HttpServletRequest request) {
        log.warn("Endpoint no encontrado: {}", request.getRequestURI());
        ErrorResponse response = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                String.format("El endpoint '%s' no existe", request.getRequestURI()),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Maneja excepciones de acceso denegado (HTTP 403).
     *
     * @param ex excepción AccessDeniedException lanzada
     * @param request objeto HttpServletRequest de la petición actual
     * @return ResponseEntity con ErrorResponse y estado FORBIDDEN (403)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex,
                                                             HttpServletRequest request) {
        log.warn("Acceso denegado: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                "No tiene permisos para acceder a este recurso",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // ========================
    // Excepcion generica (fallback)
    // ========================

    /**
     * Maneja excepciones genéricas no capturadas por otros manejadores (HTTP 500).
     * Este es un manejador de fallback para errores no controlados.
     *
     * @param ex excepción genérica lanzada
     * @param request objeto HttpServletRequest de la petición actual
     * @return ResponseEntity con ErrorResponse y estado INTERNAL_SERVER_ERROR (500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex,
                                                                 HttpServletRequest request) {
        log.error("Error interno no controlado en {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Ha ocurrido un error interno en el servidor",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
