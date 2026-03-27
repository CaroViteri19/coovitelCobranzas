package coovitelCobranza.cobranzas.interaccion.application.dto;

import coovitelCobranza.cobranzas.interaccion.domain.model.Interaccion;

import java.time.LocalDateTime;

public record InteractionResponse(
        Long id,
        Long caseId,
        String channel,
        String template,
        String result,
        LocalDateTime createdAt
) {

    public static InteractionResponse fromDomain(Interaccion interaccion) {
        return new InteractionResponse(
                interaccion.getId(),
                interaccion.getCasoGestionId(),
                interaccion.getCanal().name(),
                interaccion.getPlantilla(),
                interaccion.getResultado().name(),
                interaccion.getCreatedAt()
        );
    }
}

