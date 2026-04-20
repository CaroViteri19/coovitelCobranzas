package coovitelCobranza.cobranzas.interaction.application.exception;

public class InteractionBusinessException extends RuntimeException {

    public InteractionBusinessException(String message) {
        super(message);
    }

    public InteractionBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

