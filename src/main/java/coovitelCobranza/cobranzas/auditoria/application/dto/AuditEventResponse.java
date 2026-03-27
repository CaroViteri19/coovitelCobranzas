package coovitelCobranza.cobranzas.auditoria.application.dto;

import coovitelCobranza.cobranzas.auditoria.domain.model.AuditoriaEvento;

import java.time.LocalDateTime;

public record AuditEventResponse(
        Long id,
        String entityType,
        Long entityId,
        String action,
        String user,
        String details,
        LocalDateTime createdAt
) {

    public static AuditEventResponse fromDomain(AuditoriaEvento event) {
        return new AuditEventResponse(
                event.getId(),
                event.getEntidad(),
                event.getEntidadId(),
                event.getAccion(),
                event.getUsuario(),
                event.getDetalle(),
                event.getCreatedAt()
        );
    }
}

