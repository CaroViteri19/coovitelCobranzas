package coovitelCobranza.config.exception;

/**
 * Excepción para peticiones malformadas o datos inválidos.
 * Se lanza cuando los parámetros o el cuerpo de la petición no cumplen los requisitos esperados.
 * Retorna HTTP 400 (Bad Request).
 */
public class BadRequestException extends RuntimeException {

    /**
     * Constructor con mensaje descriptivo.
     *
     * @param message descripción del error en la petición
     */
    public BadRequestException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje y causa raíz.
     *
     * @param message descripción del error en la petición
     * @param cause excepción que causó este error
     */
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
