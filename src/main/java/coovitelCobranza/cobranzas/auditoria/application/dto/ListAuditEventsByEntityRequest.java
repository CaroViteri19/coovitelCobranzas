package coovitelCobranza.cobranzas.auditoria.application.dto;

public record ListAuditEventsByEntityRequest(
        String entityType,
        Long entityId
) {
}

