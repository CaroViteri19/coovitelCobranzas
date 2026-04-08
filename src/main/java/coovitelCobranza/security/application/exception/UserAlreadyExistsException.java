package coovitelCobranza.security.application.exception;

/**
 * Excepción lanzada cuando se intenta registrar un usuario que ya existe.
 * Se genera cuando el nombre de usuario o email ya están registrados en el sistema.
 */
public class UserAlreadyExistsException extends RuntimeException {

    /**
     * Constructor con mensaje de error.
     *
     * @param message Mensaje descriptivo del error.
     */
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}

