package coovitelCobranza.cobranzas.casogestion.application.exception;

public class CaseBusinessException extends RuntimeException {

    public CaseBusinessException(String message) {
        super(message);
    }

    public CaseBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

