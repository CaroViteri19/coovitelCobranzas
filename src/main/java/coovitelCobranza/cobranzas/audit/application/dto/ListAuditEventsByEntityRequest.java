package coovitelCobranza.cobranzas.audit.application.dto;

public record ListAuditEventsByEntityRequest(
        String entityType,
        Long entityId
) {
}

