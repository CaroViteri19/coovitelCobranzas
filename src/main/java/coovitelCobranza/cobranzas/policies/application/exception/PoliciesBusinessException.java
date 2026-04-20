package coovitelCobranza.cobranzas.policies.application.exception;

public class PoliciesBusinessException extends RuntimeException {
    public PoliciesBusinessException(String message) {
        super(message);
    }

    public PoliciesBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

