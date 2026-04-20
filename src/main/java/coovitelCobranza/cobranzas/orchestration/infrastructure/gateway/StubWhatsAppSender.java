package coovitelCobranza.cobranzas.orchestration.infrastructure.gateway;

import coovitelCobranza.cobranzas.orchestration.domain.gateway.DeliveryResult;
import coovitelCobranza.cobranzas.orchestration.domain.gateway.OutboundMessage;
import coovitelCobranza.cobranzas.orchestration.domain.gateway.WhatsAppSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Implementación stub de {@link WhatsAppSender}: sólo loguea el envío.
 *
 * <p>Activa con {@code app.orchestration.whatsapp.provider=stub} (valor por
 * defecto). Reemplazable por Twilio Business / Meta Cloud API creando otra
 * implementación con {@code havingValue} distinto.</p>
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "app.orchestration.whatsapp.provider", havingValue = "stub", matchIfMissing = true)
public class StubWhatsAppSender implements WhatsAppSender {

    @Override
    public DeliveryResult send(OutboundMessage message) {
        // TODO: integrar proveedor real (Twilio / Meta Cloud API).
        String id = "WA-STUB-" + UUID.randomUUID();
        log.info("[STUB-WHATSAPP] → to={} body={} msgId={}", message.destination(), message.body(), id);
        return DeliveryResult.ok(id);
    }
}
