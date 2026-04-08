package coovitelCobranza.cobranzas.orquestacion.domain.service;

import coovitelCobranza.cobranzas.interaccion.domain.model.Interaction;
import org.springframework.stereotype.Component;

@Component
public class OrquestadorCanalesSimulado implements OrquestadorCanales {

    @Override
    public Interaction send(Long caseId, Interaction.Channel channel, String template, String destination) {
        Interaction interaction = Interaction.create(caseId, channel, template + " -> " + destination);
        interaction.markDelivered();
        return interaction;
    }
}
