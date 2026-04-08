package coovitelCobranza.cobranzas.interaction.application.dto;

public record CreateInteractionRequest(
        Long caseId,
        String channel,
        String template
) {
}

