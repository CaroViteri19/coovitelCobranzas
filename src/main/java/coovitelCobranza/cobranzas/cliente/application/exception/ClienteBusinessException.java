package coovitelCobranza.cobranzas.cliente.application.exception;

public class ClienteBusinessException extends RuntimeException {

    public ClienteBusinessException(String message) {
        super(message);
    }

    public ClienteBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

