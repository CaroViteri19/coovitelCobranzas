package coovitelCobranza.cobranzas.payment.application.dto;

/**
 * DTO para solicitar la confirmación de un pago.
 * Contiene la referencia externa del pago que se desea confirmar.
 */
public record ConfirmPaymentRequest(
        /**
         * Referencia externa del pago a confirmar.
         */
        String reference
) {
}

