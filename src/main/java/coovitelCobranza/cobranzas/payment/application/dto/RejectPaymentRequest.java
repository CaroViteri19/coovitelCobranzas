package coovitelCobranza.cobranzas.payment.application.dto;

/**
 * DTO para solicitar el rechazo de un pago.
 */
public record RejectPaymentRequest(
        /**
         * Identificador del pago a rechazar.
         */
        Long paymentId
) {
}

