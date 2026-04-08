package coovitelCobranza.config.exception;

/**
 * Excepción para accesos no autorizados (credenciales inválidas o ausentes).
 * Se lanza cuando un usuario intenta acceder a un recurso sin proporcionar credenciales válidas.
 * Retorna HTTP 401 (Unauthorized).
 */
public class UnauthorizedException extends RuntimeException {

    /**
     * Constructor con mensaje descriptivo.
     *
     * @param message descripción del error de autenticación
     */
    public UnauthorizedException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje y causa raíz.
     *
     * @param message descripción del error de autenticación
     * @param cause excepción que causó este error
     */
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
