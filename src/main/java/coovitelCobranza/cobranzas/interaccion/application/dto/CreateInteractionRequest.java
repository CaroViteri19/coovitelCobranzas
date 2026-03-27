package coovitelCobranza.cobranzas.interaccion.application.dto;

public record CreateInteractionRequest(
        Long caseId,
        String channel,
        String template
) {
}

