package coovitelCobranza.cobranzas.politicas.application.dto;

import coovitelCobranza.cobranzas.politicas.domain.model.Politica;

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

    public static PolicyResponse fromDomain(Politica politica) {
        return new PolicyResponse(
                politica.getId(),
                politica.getEstrategiaId(),
                politica.getTipoCobro().name(),
                politica.getDiasMoraMinimo(),
                politica.getDiasMoraMaximo(),
                politica.getAccion(),
                politica.isActiva(),
                politica.getUpdatedAt()
        );
    }
}

