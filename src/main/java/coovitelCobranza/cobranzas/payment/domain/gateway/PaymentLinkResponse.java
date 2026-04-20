package coovitelCobranza.cobranzas.payment.domain.gateway;

import java.time.LocalDateTime;

/**
 * Value Object que representa la respuesta de la pasarela al solicitar un link
 * de pago.
 *
 * <p>Es inmutable (record) y vive en el dominio para que el caso de uso no
 * tenga que depender de DTOs de la capa infra.</p>
 *
 * @param paymentUrl        URL pública que se envía al cliente.
 * @param sessionToken      token de sesión devuelto por la pasarela (opcional).
 * @param expirationDate    instante en el que caduca el link.
 * @param gatewayReference  referencia única interna de la pasarela. Se usa como
 *                          {@code externalReference} del agregado Payment y para
 *                          reconciliar con el webhook.
 */
public record PaymentLinkResponse(
        String paymentUrl,
        String sessionToken,
        LocalDateTime expirationDate,
        String gatewayReference
) {
    public PaymentLinkResponse {
        if (paymentUrl == null || paymentUrl.isBlank()) {
            throw new IllegalArgumentException("paymentUrl is required");
        }
        if (gatewayReference == null || gatewayReference.isBlank()) {
            throw new IllegalArgumentException("gatewayReference is required");
        }
    }
}
