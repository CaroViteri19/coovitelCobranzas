package coovitelCobranza.cobranzas.casogestion.application.dto;

public record AssignAdvisorRequest(
        String advisor,
        String performedBy,
        String performedByRole,
        String assignmentSource,
        String correlationId
) {
}
