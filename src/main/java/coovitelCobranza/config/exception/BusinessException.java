package coovitelCobranza.config.exception;

/**
 * Excepción base para violaciones de reglas de negocio.
 * Retorna HTTP 400 (Bad Request).
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
