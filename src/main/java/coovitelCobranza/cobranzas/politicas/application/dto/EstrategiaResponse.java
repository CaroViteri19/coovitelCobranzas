package coovitelCobranza.cobranzas.politicas.application.dto;

import coovitelCobranza.cobranzas.politicas.domain.model.Estrategia;

import java.time.LocalDateTime;

public record EstrategiaResponse(
        Long id,
        String nombre,
        String descripcion,
        boolean activa,
        int maxIntentosContacto,
        int diasAntesDeeEscalacion,
        String rolAsignacionEscalada,
        LocalDateTime updatedAt
) {

    public static EstrategiaResponse fromDomain(Estrategia estrategia) {
        return new EstrategiaResponse(
                estrategia.getId(),
                estrategia.getNombre(),
                estrategia.getDescripcion(),
                estrategia.isActiva(),
                estrategia.getMaxIntentosContacto(),
                estrategia.getDiasAntesDeeEscalacion(),
                estrategia.getRolAsignacionEscalada(),
                estrategia.getUpdatedAt()
        );
    }
}

