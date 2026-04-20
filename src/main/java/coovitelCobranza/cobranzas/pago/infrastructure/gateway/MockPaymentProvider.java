package coovitelCobranza.cobranzas.pago.infrastructure.gateway;

import coovitelCobranza.cobranzas.obligacion.domain.model.Obligation;
import coovitelCobranza.cobranzas.pago.domain.gateway.PaymentLinkResponse;
import coovitelCobranza.cobranzas.pago.domain.gateway.PaymentProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementación simulada de {@link PaymentProvider}.
 *
 * <p>Activa con {@code app.payments.provider=mock} (valor por defecto) o en cualquier
 * entorno local/dev/test. No hace llamadas HTTP; genera URLs y tokens sintéticos.</p>
 *
 * <p>Se registra como bean <em>solo</em> si la propiedad está en {@code mock}, de modo
 * que en producción automáticamente se usa {@link WompiPaymentProvider}.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.payments.provider", havingValue = "mock", matchIfMissing = true)
public class MockPaymentProvider implements PaymentProvider {

    /**
     * Base de la URL de pago simulada. Puede apuntar al frontend local o a un stub.
     */
    @Value("${app.payments.mock.base-url:http://localhost:8080/mock-checkout}")
    private String baseUrl;

    /**
     * Minutos hasta que expira el link.
     */
    @Value("${app.payments.mock.expiration-minutes:30}")
    private int expirationMinutes;

    @Override
    public PaymentLinkResponse generateLink(Obligation obligacion) {
        String sessionToken = UUID.randomUUID().toString();
        String referenciaPasarela = "MOCK-" + UUID.randomUUID();
        String urlPago = String.format("%s?session=%s&ref=%s&amount=%s",
                baseUrl, sessionToken, referenciaPasarela, obligacion.getTotalBalance().toPlainString());
        LocalDateTime fechaExpiracion = LocalDateTime.now().plusMinutes(expirationMinutes);

        log.info("[MOCK] Link generado para obligacion={} ref={} expira={}",
                obligacion.getObligationNumber(), referenciaPasarela, fechaExpiracion);

        return new PaymentLinkResponse(urlPago, sessionToken, fechaExpiracion, referenciaPasarela);
    }
}
