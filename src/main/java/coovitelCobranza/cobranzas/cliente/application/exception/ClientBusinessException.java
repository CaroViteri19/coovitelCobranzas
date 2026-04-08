package coovitelCobranza.cobranzas.cliente.application.exception;

public class ClientBusinessException extends RuntimeException {

    public ClientBusinessException(String message) {
        super(message);
    }

    public ClientBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

