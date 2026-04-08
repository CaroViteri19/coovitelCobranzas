package coovitelCobranza.cobranzas.politicas.application.dto;

import coovitelCobranza.cobranzas.politicas.domain.model.Policy;

import java.time.LocalDateTime;

public record PolicyResponse(
        Long id,
        Long strategyId,
        String collectionType,
        int minDelinquencyDays,
        int maxDelinquencyDays,
        String action,
        boolean active,
        LocalDateTime updatedAt
) {

    public static PolicyResponse fromDomain(Policy politica) {
        return new PolicyResponse(
                politica.getId(),
                politica.getStrategyId(),
                politica.getTipoCobro().name(),
                politica.getDiasDelinquencyMinimo(),
                politica.getDiasDelinquencyMaximo(),
                politica.getAction(),
                politica.isActiva(),
                politica.getUpdatedAt()
        );
    }
}

