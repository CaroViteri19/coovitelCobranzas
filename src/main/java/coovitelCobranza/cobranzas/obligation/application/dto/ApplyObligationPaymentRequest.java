package coovitelCobranza.cobranzas.obligation.application.dto;

import java.math.BigDecimal;

/**
 * DTO para solicitar la aplicación de un pago a una obligación.
 * Contiene el identificador de la obligación y el monto del pago a aplicar.
 *
 * @param obligationId Identificador único de la obligación a pagar
 * @param paymentAmount Monto del pago a aplicar a la obligación
 */
public record ApplyObligationPaymentRequest(Long obligationId, BigDecimal paymentAmount) {
}

