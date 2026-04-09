package coovitelCobranza.security.application.exception;

/**
 * Excepción lanzada cuando las credenciales de autenticación son inválidas.
 * Se genera cuando el usuario o contraseña no coinciden con los registros.
 */
public class InvalidCredentialsException extends RuntimeException {

    /**
     * Constructor con mensaje de error.
     *
     * @param message Mensaje descriptivo del error.
     */
    public InvalidCredentialsException(String message) {
        super(message);
    }
}

