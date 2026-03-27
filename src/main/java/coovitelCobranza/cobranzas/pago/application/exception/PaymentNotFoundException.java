package coovitelCobranza.cobranzas.pago.application.exception;

public class PaymentNotFoundException extends RuntimeException {

    public PaymentNotFoundException(Long id) {
        super("Payment not found with ID: " + id);
    }

    public PaymentNotFoundException(String externalReference) {
        super("Payment not found with reference: " + externalReference);
    }
}

