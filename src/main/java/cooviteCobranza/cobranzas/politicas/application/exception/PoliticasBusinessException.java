package cooviteCobranza.cobranzas.politicas.application.exception;

public class PoliticasBusinessException extends RuntimeException {
    public PoliticasBusinessException(String message) {
        super(message);
    }

    public PoliticasBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

