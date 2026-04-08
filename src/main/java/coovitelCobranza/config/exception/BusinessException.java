package coovitelCobranza.config.exception;

/**
 * Excepción base para violaciones de reglas de negocio.
 * Se lanza cuando una operación viola una regla de negocio del sistema.
 * Retorna HTTP 400 (Bad Request).
 */
public class BusinessException extends RuntimeException {

    /**
     * Constructor con mensaje descriptivo.
     *
     * @param message descripción del error de negocio
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje y causa raíz.
     *
     * @param message descripción del error de negocio
     * @param cause excepción que causó este error
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
