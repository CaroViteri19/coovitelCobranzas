package coovitelCobranza.cobranzas.casogestion.application.dto;

import coovitelCobranza.cobranzas.casogestion.domain.model.CasoGestion;

import java.time.LocalDateTime;

public record CaseResponse(
        Long id,
        Long obligationId,
        String priority,
        String status,
        String assignedAdvisor,
        LocalDateTime nextActionAt,
        LocalDateTime updatedAt
) {

    public static CaseResponse fromDomain(CasoGestion casoGestion) {
        return new CaseResponse(
                casoGestion.getId(),
                casoGestion.getObligacionId(),
                casoGestion.getPrioridad().name(),
                casoGestion.getEstado().name(),
                casoGestion.getAsesorAsignado(),
                casoGestion.getProximaAccionAt(),
                casoGestion.getUpdatedAt()
        );
    }
}

