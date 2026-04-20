package coovitelCobranza.cobranzas.policies.application.dto;

public record CreateStrategyRequest(
        String name,
        String description,
        int maxContactAttempts,
        int daysBeforeEscalation,
        String escalationAssignmentRole
) {
}

