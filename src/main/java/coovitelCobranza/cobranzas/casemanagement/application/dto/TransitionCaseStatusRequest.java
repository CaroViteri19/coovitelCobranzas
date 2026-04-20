package coovitelCobranza.cobranzas.casemanagement.application.dto;

public record TransitionCaseStatusRequest(
        Long caseId,
        String targetStatus,
        String reason,
        String performedBy,
        String performedByRole,
        String source,
        String correlationId
) {
}

