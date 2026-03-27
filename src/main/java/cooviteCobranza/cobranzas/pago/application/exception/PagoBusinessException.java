package cooviteCobranza.cobranzas.pago.application.exception;

public class PagoBusinessException extends RuntimeException {

    public PagoBusinessException(String message) {
        super(message);
    }

    public PagoBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

