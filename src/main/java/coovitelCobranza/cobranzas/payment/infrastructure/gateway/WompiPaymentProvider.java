package coovitelCobranza.cobranzas.payment.infrastructure.gateway;

import coovitelCobranza.cobranzas.obligation.domain.model.Obligation;
import coovitelCobranza.cobranzas.payment.domain.gateway.PaymentGatewayException;
import coovitelCobranza.cobranzas.payment.domain.gateway.PaymentLinkResponse;
import coovitelCobranza.cobranzas.payment.domain.gateway.PaymentProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Esqueleto del adaptador oficial de Wompi (Bancolombia).
 *
 * <p>Sólo se instancia con {@code app.payments.provider=wompi}. La llamada
 * HTTP real está pendiente (TODO): cuando se tenga el contrato/kit, aquí se
 * invoca al endpoint {@code POST /payment_links} con {@code publicKey} y
 * firma HMAC; la respuesta se mapea a {@link PaymentLinkResponse}.</p>
 *
 * <p>Mientras tanto, esta implementación lanza un {@link PaymentGatewayException}
 * para evitar que alguien habilite {@code wompi} sin terminar la integración.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(WompiProperties.class)
@ConditionalOnProperty(name = "app.payments.provider", havingValue = "wompi")
public class WompiPaymentProvider implements PaymentProvider {

    private final WompiProperties properties;

    @Override
    public PaymentLinkResponse generateLink(Obligation obligation) {
        log.warn("[WOMPI] generateLink invocado con baseUrl={} publicKey={}... (skeleton)",
                properties.getBaseUrl(),
                properties.getPublicKey() != null ? properties.getPublicKey().substring(0, Math.min(6, properties.getPublicKey().length())) : "null");

        // TODO: implementar la integración real:
        //   1. Construir payload (amount_in_cents, currency=COP, reference=obligación, expires_at, redirect_url).
        //   2. Firmar con HMAC usando privateKey / integritySecret.
        //   3. POST https://production.wompi.co/v1/payment_links con Authorization: Bearer publicKey.
        //   4. Parsear respuesta → PaymentLinkResponse(id, checkoutUrl, expiresAt).
        //   5. Manejar 4xx/5xx → PaymentGatewayException.

        // Stub de desarrollo para no romper en caliente si alguien activa la propiedad:
        if (properties.getBaseUrl() == null || properties.getBaseUrl().isBlank()) {
            throw new PaymentGatewayException("Wompi no está configurado (app.payments.wompi.base-url vacío)");
        }

        String gatewayReference = "WOMPI-PENDING-" + UUID.randomUUID();
        return new PaymentLinkResponse(
                properties.getBaseUrl() + "/checkout/" + gatewayReference,
                null,
                LocalDateTime.now().plusMinutes(30),
                gatewayReference
        );
    }
}
