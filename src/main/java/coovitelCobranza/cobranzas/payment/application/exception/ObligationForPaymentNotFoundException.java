package coovitelCobranza.cobranzas.payment.application.exception;

/**
 * Excepción lanzada por el módulo de pagos cuando no existe la obligación
 * asociada a la operación (generar link, confirmar pago).
 */
public class ObligationForPaymentNotFoundException extends RuntimeException {

    public ObligationForPaymentNotFoundException(Long obligationId) {
        super("Obligation not found for payment: id=" + obligationId);
    }
}
