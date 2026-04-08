package coovitelCobranza.config.exception;

/**
 * Excepción para accesos prohibidos (sin permisos suficientes).
 * Retorna HTTP 403 (Forbidden).
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
