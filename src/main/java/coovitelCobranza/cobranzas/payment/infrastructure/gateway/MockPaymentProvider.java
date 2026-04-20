package coovitelCobranza.cobranzas.payment.infrastructure.gateway;

import coovitelCobranza.cobranzas.obligation.domain.model.Obligation;
import coovitelCobranza.cobranzas.payment.domain.gateway.PaymentLinkResponse;
import coovitelCobranza.cobranzas.payment.domain.gateway.PaymentProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementación simulada de {@link PaymentProvider}.
 *
 * <p>Activa con {@code app.payments.provider=mock} (valor por defecto). No
 * hace llamadas HTTP: genera URLs y tokens sintéticos y los loguea. Ideal
 * para desarrollo, pruebas end-to-end y demos antes de tener firmado el
 * contrato con la pasarela real.</p>
 *
 * <p>Se registra como bean <em>sólo</em> si la propiedad está en {@code mock};
 * así en producción automáticamente se usa {@link WompiPaymentProvider}.</p>
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "app.payments.provider", havingValue = "mock", matchIfMissing = true)
public class MockPaymentProvider implements PaymentProvider {

    /** Base de la URL de pago simulada. Puede apuntar al front local o a un stub. */
    @Value("${app.payments.mock.base-url:http://localhost:8080/mock-checkout}")
    private String baseUrl;

    /** Minutos hasta que expira el link. */
    @Value("${app.payments.mock.expiration-minutes:30}")
    private int expirationMinutes;

    @Override
    public PaymentLinkResponse generateLink(Obligation obligation) {
        String sessionToken = UUID.randomUUID().toString();
        String gatewayReference = "MOCK-" + UUID.randomUUID();
        String paymentUrl = String.format("%s?session=%s&ref=%s&amount=%s",
                baseUrl, sessionToken, gatewayReference, obligation.getTotalBalance().toPlainString());
        LocalDateTime expirationDate = LocalDateTime.now().plusMinutes(expirationMinutes);

        log.info("[MOCK] Link generated for obligation={} ref={} expires={}",
                obligation.getObligationNumber(), gatewayReference, expirationDate);

        return new PaymentLinkResponse(paymentUrl, sessionToken, expirationDate, gatewayReference);
    }
}
