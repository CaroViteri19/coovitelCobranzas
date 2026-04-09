package coovitelCobranza.cobranzas.casogestion.application.exception;

/**
 * Excepción que se lanza cuando ocurre una violación de una regla de negocio
 * durante la operación con casos de cobranza.
 */
public class CaseBusinessException extends RuntimeException {

    /**
     * Construye una excepción con el mensaje especificado.
     *
     * @param message el mensaje de error descriptivo
     */
    public CaseBusinessException(String message) {
        super(message);
    }

    /**
     * Construye una excepción con el mensaje y la causa especificados.
     *
     * @param message el mensaje de error descriptivo
     * @param cause la excepción que causó este error
     */
    public CaseBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

