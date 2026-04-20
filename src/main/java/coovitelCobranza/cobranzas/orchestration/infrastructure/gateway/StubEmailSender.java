package coovitelCobranza.cobranzas.orchestration.infrastructure.gateway;

import coovitelCobranza.cobranzas.orchestration.domain.gateway.DeliveryResult;
import coovitelCobranza.cobranzas.orchestration.domain.gateway.EmailSender;
import coovitelCobranza.cobranzas.orchestration.domain.gateway.OutboundMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Implementación stub de {@link EmailSender}: sólo loguea el envío.
 *
 * <p>Activa con {@code app.orchestration.email.provider=stub} (valor por
 * defecto). Reemplazable por SendGrid/SES/SMTP creando otra implementación
 * con {@code havingValue} distinto.</p>
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "app.orchestration.email.provider", havingValue = "stub", matchIfMissing = true)
public class StubEmailSender implements EmailSender {

    @Value("${app.orchestration.email.from-address:no-reply@coovitel.local}")
    private String fromAddress;

    @Override
    public DeliveryResult send(OutboundMessage message) {
        // TODO: integrar proveedor real (SendGrid / SES / SMTP).
        String id = "EMAIL-STUB-" + UUID.randomUUID();
        log.info("[STUB-EMAIL] from={} → to={} subject={} body={} msgId={}",
                fromAddress, message.destination(), message.subject(), message.body(), id);
        return DeliveryResult.ok(id);
    }
}
