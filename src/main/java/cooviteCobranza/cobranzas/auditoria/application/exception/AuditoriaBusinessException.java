package cooviteCobranza.cobranzas.auditoria.application.exception;

public class AuditoriaBusinessException extends RuntimeException {

    public AuditoriaBusinessException(String message) {
        super(message);
    }

    public AuditoriaBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

