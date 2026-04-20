package coovitelCobranza.cobranzas.client.application.exception;

/**
 * Excepción lanzada cuando se viola una regla de negocio relacionada con clientes.
 * Ejemplos: cliente duplicado, datos inválidos, operación no permitida.
 */
public class ClientBusinessException extends RuntimeException {

    /**
     * Construye una excepción de regla de negocio con un mensaje.
     *
     * @param message descripción del error
     */
    public ClientBusinessException(String message) {
        super(message);
    }

    /**
     * Construye una excepción de regla de negocio con mensaje y causa raíz.
     *
     * @param message descripción del error
     * @param cause excepción que originó este error
     */
    public ClientBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

