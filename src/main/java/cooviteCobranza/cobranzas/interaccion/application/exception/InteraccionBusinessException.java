package cooviteCobranza.cobranzas.interaccion.application.exception;

public class InteraccionBusinessException extends RuntimeException {

    public InteraccionBusinessException(String message) {
        super(message);
    }

    public InteraccionBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

