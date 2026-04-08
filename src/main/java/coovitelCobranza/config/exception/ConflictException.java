package coovitelCobranza.config.exception;

/**
 * Excepción para conflictos de estado (recurso duplicado, estado inconsistente).
 * Se lanza cuando una operación entra en conflicto con el estado actual de un recurso.
 * Retorna HTTP 409 (Conflict).
 */
public class ConflictException extends RuntimeException {

    /**
     * Constructor con mensaje descriptivo.
     *
     * @param message descripción del conflicto detectado
     */
    public ConflictException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje y causa raíz.
     *
     * @param message descripción del conflicto detectado
     * @param cause excepción que causó este error
     */
    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
