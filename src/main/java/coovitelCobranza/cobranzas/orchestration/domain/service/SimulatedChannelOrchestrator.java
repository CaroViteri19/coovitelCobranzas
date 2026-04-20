package coovitelCobranza.cobranzas.orchestration.domain.service;

import coovitelCobranza.cobranzas.interaction.domain.model.Interaction;
import coovitelCobranza.cobranzas.orchestration.domain.gateway.DeliveryResult;
import coovitelCobranza.cobranzas.orchestration.domain.gateway.EmailSender;
import coovitelCobranza.cobranzas.orchestration.domain.gateway.OutboundMessage;
import coovitelCobranza.cobranzas.orchestration.domain.gateway.SmsSender;
import coovitelCobranza.cobranzas.orchestration.domain.gateway.WhatsAppSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Implementación por defecto de {@link ChannelOrchestrator} que delega a los
 * puertos de cada canal.
 *
 * <p>Antes esta clase generaba una Interaction "simulada" sin hablar con
 * ningún proveedor. Ahora cada canal se envía a través de su {@code Sender}
 * (stubs por defecto, reales cuando se configuren), y el resultado técnico
 * del proveedor se refleja en el estado final de la {@link Interaction}.</p>
 *
 * <p>El canal {@code VOICE} no tiene sender todavía: se crea la interacción
 * en {@code PENDING} (se marca cuando entre la integración telefónica).</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SimulatedChannelOrchestrator implements ChannelOrchestrator {

    private final SmsSender smsSender;
    private final EmailSender emailSender;
    private final WhatsAppSender whatsAppSender;

    @Override
    public Interaction send(Long caseId, Interaction.Channel channel, String template, String destination) {
        Interaction interaction = Interaction.create(caseId, channel, template + " -> " + destination);

        DeliveryResult result = switch (channel) {
            case SMS      -> smsSender.send(OutboundMessage.text(destination, template));
            case WHATSAPP -> whatsAppSender.send(OutboundMessage.text(destination, template));
            case EMAIL    -> emailSender.send(OutboundMessage.email(destination, "Notificación Coovitel", template));
            case VOICE    -> DeliveryResult.failed("Voice channel not wired yet");
        };

        if (result.success()) {
            interaction.markDelivered();
        } else {
            log.warn("Envío {} falló a {} -> {}", channel, destination, result.errorDescription());
            interaction.markFailed();
        }
        return interaction;
    }
}
