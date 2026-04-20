package coovitelCobranza.cobranzas.payment.application.dto;

import coovitelCobranza.cobranzas.payment.domain.gateway.PaymentLinkResponse;

import java.time.LocalDateTime;

/**
 * DTO de salida al generar un link de pago.
 * Combina el id del Payment persistido en estado PENDING con los datos de
 * la pasarela que debe conocer el frontend/cliente.
 */
public record GenerateLinkResponse(
        Long paymentId,
        String paymentUrl,
        String sessionToken,
        LocalDateTime expirationDate,
        String gatewayReference
) {
    public static GenerateLinkResponse of(Long paymentId, PaymentLinkResponse link) {
        return new GenerateLinkResponse(
                paymentId,
                link.paymentUrl(),
                link.sessionToken(),
                link.expirationDate(),
                link.gatewayReference()
        );
    }
}
