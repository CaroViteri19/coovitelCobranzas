package coovitelCobranza.cobranzas.politicas.application.dto;

import coovitelCobranza.cobranzas.politicas.domain.model.Estrategia;

import java.time.LocalDateTime;

public record StrategyResponse(
        Long id,
        String name,
        String description,
        boolean active,
        int maxContactAttempts,
        int daysBeforeEscalation,
        String escalationAssignmentRole,
        LocalDateTime updatedAt
) {

    public static StrategyResponse fromDomain(Estrategia estrategia) {
        return new StrategyResponse(
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

