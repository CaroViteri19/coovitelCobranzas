package coovitelCobranza.cobranzas.payment.application.dto;

/**
 * DTO para solicitar el listado de pagos de una obligación.
 */
public record ListPaymentsByObligationRequest(
        /**
         * Identificador de la obligación cuyos pagos se desean listar.
         */
        Long obligationId
) {
}

