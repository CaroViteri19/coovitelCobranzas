package coovitelCobranza.cobranzas.payment.application.dto;

import java.math.BigDecimal;

/**
 * DTO para solicitar la creación de un nuevo pago.
 * Contiene la información necesaria para registrar un pago en el sistema.
 */
public record CreatePaymentRequest(
        /**
         * Identificador de la obligación que se está pagando.
         */
        Long obligationId,
        /**
         * Monto del pago a procesar.
         */
        BigDecimal amount,
        /**
         * Referencia externa del pago proporcionada por el banco o pasarela.
         */
        String externalReference,
        /**
         * Método de pago utilizado (PSE, CARD, TRANSFER, OFFICE).
         */
        String method
) {
}

