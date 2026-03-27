package coovitelCobranza.cobranzas.interaccion.application.dto;

import coovitelCobranza.cobranzas.interaccion.domain.model.Interaccion;

import java.time.LocalDateTime;

public record InteraccionResponse(
        Long id,
        Long casoGestionId,
        String canal,
        String plantilla,
        String resultado,
        LocalDateTime createdAt
) {

    public static InteraccionResponse fromDomain(Interaccion interaccion) {
        return new InteraccionResponse(
                interaccion.getId(),
                interaccion.getCasoGestionId(),
                interaccion.getCanal().name(),
                interaccion.getPlantilla(),
                interaccion.getResultado().name(),
                interaccion.getCreatedAt()
        );
    }
}

