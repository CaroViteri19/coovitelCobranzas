package coovitelCobranza.config.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Record que representa una respuesta de error estandarizada para la API.
 * Contiene información sobre el error, su causa y detalles de validación si aplica.
 *
 * @param timestamp marca de tiempo en la que ocurrió el error (formato: yyyy-MM-dd'T'HH:mm:ss)
 * @param status código de estado HTTP del error
 * @param error nombre corto del tipo de error
 * @param message mensaje descriptivo del error
 * @param path ruta del endpoint donde ocurrió el error
 * @param fieldErrors lista de errores de validación de campos (opcional)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldError> fieldErrors
) {

    /**
     * Constructor simplificado sin errores de validación de campos.
     *
     * @param status código de estado HTTP
     * @param error nombre del tipo de error
     * @param message mensaje del error
     * @param path ruta del endpoint
     */
    public ErrorResponse(int status, String error, String message, String path) {
        this(LocalDateTime.now(), status, error, message, path, null);
    }

    /**
     * Constructor con errores de validación de campos.
     *
     * @param status código de estado HTTP
     * @param error nombre del tipo de error
     * @param message mensaje del error
     * @param path ruta del endpoint
     * @param fieldErrors lista de errores de validación específicos de campos
     */
    public ErrorResponse(int status, String error, String message, String path, List<FieldError> fieldErrors) {
        this(LocalDateTime.now(), status, error, message, path, fieldErrors);
    }

    /**
     * Record que representa un error de validación en un campo específico.
     *
     * @param field nombre del campo que falló la validación
     * @param message mensaje de error de validación
     */
    public record FieldError(
            String field,
            String message
    ) {
    }
}
