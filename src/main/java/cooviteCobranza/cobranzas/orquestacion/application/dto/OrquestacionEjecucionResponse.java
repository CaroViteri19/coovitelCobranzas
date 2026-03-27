package cooviteCobranza.cobranzas.orquestacion.application.dto;

import cooviteCobranza.cobranzas.orquestacion.domain.model.OrquestacionEjecucion;

import java.time.LocalDateTime;

public record OrquestacionEjecucionResponse(
        Long id,
        Long casoGestionId,
        String canal,
        String destino,
        String plantilla,
        String estado,
        LocalDateTime createdAt
) {

    public static OrquestacionEjecucionResponse fromDomain(OrquestacionEjecucion ejecucion) {
        return new OrquestacionEjecucionResponse(
                ejecucion.getId(),
                ejecucion.getCasoGestionId(),
                ejecucion.getCanal(),
                ejecucion.getDestino(),
                ejecucion.getPlantilla(),
                ejecucion.getEstado().name(),
                ejecucion.getCreatedAt()
        );
    }
}

