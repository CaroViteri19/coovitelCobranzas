package coovitelCobranza.cobranzas.orchestration.domain.service;

import coovitelCobranza.cobranzas.interaction.domain.model.Interaction;

public interface ChannelOrchestrator {

    Interaction send(Long caseId, Interaction.Channel channel, String template, String destination);
}
