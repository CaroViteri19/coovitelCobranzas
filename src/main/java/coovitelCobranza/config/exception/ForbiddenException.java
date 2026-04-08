package coovitelCobranza.config.exception;

/**
 * Excepción para accesos prohibidos (sin permisos suficientes).
 * Se lanza cuando un usuario autenticado intenta acceder a un recurso sin los permisos necesarios.
 * Retorna HTTP 403 (Forbidden).
 */
public class ForbiddenException extends RuntimeException {

    /**
     * Constructor con mensaje descriptivo.
     *
     * @param message descripción del error de autorización
     */
    public ForbiddenException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje y causa raíz.
     *
     * @param message descripción del error de autorización
     * @param cause excepción que causó este error
     */
    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
