package coovitelCobranza.config.exception;

/**
 * Excepción para accesos no autorizados (credenciales inválidas o ausentes).
 * Retorna HTTP 401 (Unauthorized).
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
