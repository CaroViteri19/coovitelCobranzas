package coovitelCobranza.cobranzas.orquestacion.application.exception;

public class OrquestacionBusinessException extends RuntimeException {

    public OrquestacionBusinessException(String message) {
        super(message);
    }

    public OrquestacionBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

