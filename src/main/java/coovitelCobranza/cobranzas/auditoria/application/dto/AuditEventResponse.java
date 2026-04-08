package coovitelCobranza.cobranzas.auditoria.application.dto;

import coovitelCobranza.cobranzas.auditoria.domain.model.AuditEvent;

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

    public static AuditEventResponse fromDomain(AuditEvent event) {
        return new AuditEventResponse(
                event.getId(),
                event.getEntidad(),
                event.getEntidadId(),
                event.getAction(),
                event.getUsuario(),
                event.getDetalle(),
                event.getCreatedAt()
        );
    }
}

