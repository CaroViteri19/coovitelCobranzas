package coovitelCobranza.cobranzas.payment.application.dto;

import coovitelCobranza.cobranzas.payment.domain.model.Payment;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de entrada para generar un link de pago.
 *
 * @param obligationId id de la obligación a cobrar.
 * @param method       método propuesto (PSE, CARD, TRANSFER, OFFICE).
 */
public record GenerateLinkRequest(
        @NotNull(message = "obligationId is required") Long obligationId,
        @NotNull(message = "method is required") Payment.PaymentMethod method
) {
}
