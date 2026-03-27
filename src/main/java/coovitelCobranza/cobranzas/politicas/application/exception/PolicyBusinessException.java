package coovitelCobranza.cobranzas.politicas.application.exception;

public class PolicyBusinessException extends RuntimeException {
    public PolicyBusinessException(String message) {
        super(message);
    }

    public PolicyBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

