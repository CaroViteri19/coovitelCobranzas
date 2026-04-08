package coovitelCobranza.cobranzas.pago.application.dto;

/**
 * DTO para solicitar un pago por su referencia externa.
 */
public record GetPaymentByReferenceRequest(
        /**
         * Referencia externa del pago a recuperar (proporcionada por el banco).
         */
        String externalReference
) {
}

