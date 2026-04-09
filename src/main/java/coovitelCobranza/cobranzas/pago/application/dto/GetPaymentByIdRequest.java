package coovitelCobranza.cobranzas.pago.application.dto;

/**
 * DTO para solicitar un pago por su identificador único.
 */
public record GetPaymentByIdRequest(
        /**
         * Identificador único del pago a recuperar.
         */
        Long paymentId
) {
}

