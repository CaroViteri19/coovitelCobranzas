package coovitelCobranza.cobranzas.audit.application.dto;

import coovitelCobranza.cobranzas.audit.domain.model.AuditEvent;

import java.time.LocalDateTime;

public record AuditEventResponse(
        Long id,
        String module,
        String entityType,
        Long entityId,
        String action,
        String user,
        String userRole,
        String source,
        String correlationId,
        String details,
        LocalDateTime createdAt
) {

    public static AuditEventResponse fromDomain(AuditEvent event) {
        return new AuditEventResponse(
                event.getId(),
                event.getModule(),
                event.getEntityType(),
                event.getEntityId(),
                event.getAction(),
                event.getUser(),
                event.getUserRole(),
                event.getSource(),
                event.getCorrelationId(),
                event.getDetails(),
                event.getCreatedAt()
        );
    }
}
