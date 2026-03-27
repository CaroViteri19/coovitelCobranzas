package coovitelCobranza.cobranzas.auditoria.application.dto;

public record RegisterAuditRequest(
        String entityType,
        Long entityId,
        String action,
        String user,
        String details
) {
}

