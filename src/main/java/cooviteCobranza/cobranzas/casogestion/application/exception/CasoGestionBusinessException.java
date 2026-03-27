package cooviteCobranza.cobranzas.casogestion.application.exception;

public class CasoGestionBusinessException extends RuntimeException {

    public CasoGestionBusinessException(String message) {
        super(message);
    }

    public CasoGestionBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

