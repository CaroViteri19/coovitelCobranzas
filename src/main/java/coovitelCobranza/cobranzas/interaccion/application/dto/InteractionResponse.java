package coovitelCobranza.cobranzas.interaccion.application.dto;

import coovitelCobranza.cobranzas.interaccion.domain.model.Interaction;

import java.time.LocalDateTime;

public record InteractionResponse(
        Long id,
        Long caseId,
        String channel,
        String template,
        String result,
        LocalDateTime createdAt
) {

    public static InteractionResponse fromDomain(Interaction interaction) {
        return new InteractionResponse(
                interaction.getId(),
                interaction.getCaseId(),
                interaction.getChannel().name(),
                interaction.getTemplate(),
                interaction.getResultStatus().name(),
                interaction.getCreatedAt()
        );
    }
}

