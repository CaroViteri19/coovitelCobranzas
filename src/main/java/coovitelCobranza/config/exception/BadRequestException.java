package coovitelCobranza.config.exception;

/**
 * Excepción para peticiones malformadas o datos inválidos.
 * Retorna HTTP 400 (Bad Request).
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
