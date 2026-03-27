package coovitelCobranza.cobranzas.pago.application.exception;

public class PaymentBusinessException extends RuntimeException {

    public PaymentBusinessException(String message) {
        super(message);
    }

    public PaymentBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

