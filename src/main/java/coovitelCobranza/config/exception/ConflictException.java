package coovitelCobranza.config.exception;

/**
 * Excepción para conflictos de estado (recurso duplicado, estado inconsistente).
 * Retorna HTTP 409 (Conflict).
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
