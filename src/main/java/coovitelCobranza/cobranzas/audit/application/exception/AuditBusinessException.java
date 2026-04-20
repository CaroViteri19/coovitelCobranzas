package coovitelCobranza.cobranzas.audit.application.exception;

public class AuditBusinessException extends RuntimeException {

    public AuditBusinessException(String message) {
        super(message);
    }

    public AuditBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

