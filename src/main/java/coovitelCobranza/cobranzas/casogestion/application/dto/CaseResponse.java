package coovitelCobranza.cobranzas.casogestion.application.dto;

import coovitelCobranza.cobranzas.casogestion.domain.model.Case;

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

    public static CaseResponse fromDomain(Case casoGestion) {
        return new CaseResponse(
                casoGestion.getId(),
                casoGestion.getObligationId(),
                casoGestion.getPriority().name(),
                casoGestion.getStatus().name(),
                casoGestion.getAssignedAdvisor(),
                casoGestion.getNextActionAt(),
                casoGestion.getUpdatedAt()
        );
    }
}

