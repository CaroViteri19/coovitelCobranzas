package coovitelCobranza.cobranzas.orchestration.domain.service;

import coovitelCobranza.cobranzas.interaction.domain.model.Interaction;
import org.springframework.stereotype.Component;

@Component
public class SimulatedChannelOrchestrator implements ChannelOrchestrator {

    @Override
    public Interaction send(Long caseId, Interaction.Channel channel, String template, String destination) {
        Interaction interaction = Interaction.create(caseId, channel, template + " -> " + destination);
        interaction.markDelivered();
        return interaction;
    }
}
