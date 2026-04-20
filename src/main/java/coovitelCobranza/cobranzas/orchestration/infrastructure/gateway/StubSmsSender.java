package coovitelCobranza.cobranzas.orchestration.infrastructure.gateway;

import coovitelCobranza.cobranzas.orchestration.domain.gateway.DeliveryResult;
import coovitelCobranza.cobranzas.orchestration.domain.gateway.OutboundMessage;
import coovitelCobranza.cobranzas.orchestration.domain.gateway.SmsSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Implementación stub de {@link SmsSender}: sólo loguea el envío.
 *
 * <p>Se activa con {@code app.orchestration.sms.provider=stub} (valor por
 * defecto). Cuando se tenga cuenta con el proveedor real (Twilio, Infobip,
 * Avantel...), crear otra implementación con
 * {@code @ConditionalOnProperty(havingValue = "twilio")} y la propiedad
 * apuntará a esa.</p>
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "app.orchestration.sms.provider", havingValue = "stub", matchIfMissing = true)
public class StubSmsSender implements SmsSender {

    @Override
    public DeliveryResult send(OutboundMessage message) {
        // TODO: integrar proveedor real (Twilio / Infobip / Avantel).
        String id = "SMS-STUB-" + UUID.randomUUID();
        log.info("[STUB-SMS] → to={} body={} msgId={}", message.destination(), message.body(), id);
        return DeliveryResult.ok(id);
    }
}
