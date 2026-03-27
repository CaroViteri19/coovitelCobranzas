package cooviteCobranza.cobranzas.casogestion.application.dto;

import cooviteCobranza.cobranzas.casogestion.domain.model.CasoGestion;

import java.time.LocalDateTime;

public record CasoGestionResponse(
        Long id,
        Long obligacionId,
        String prioridad,
        String estado,
        String asesorAsignado,
        LocalDateTime proximaAccionAt,
        LocalDateTime updatedAt
) {

    public static CasoGestionResponse fromDomain(CasoGestion casoGestion) {
        return new CasoGestionResponse(
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

