package coovitelCobranza.cobranzas.orquestacion.application.exception;

public class OrchestrationBusinessException extends RuntimeException {

    public OrchestrationBusinessException(String message) {
        super(message);
    }

    public OrchestrationBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

