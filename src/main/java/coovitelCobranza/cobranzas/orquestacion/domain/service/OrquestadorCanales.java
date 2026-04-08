package coovitelCobranza.cobranzas.orquestacion.domain.service;

import coovitelCobranza.cobranzas.interaction.domain.model.Interaction;

public interface OrquestadorCanales {

    Interaction send(Long caseId, Interaction.Channel channel, String template, String destination);
}
