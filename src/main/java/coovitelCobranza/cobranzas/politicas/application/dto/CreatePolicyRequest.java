package coovitelCobranza.cobranzas.politicas.application.dto;

public record CreatePolicyRequest(
        Long strategyId,
        String collectionType,
        int minDelinquencyDays,
        int maxDelinquencyDays,
        String action
) {
}

