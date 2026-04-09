package coovitelCobranza.cobranzas.auditoria.application.dto;

public record RegisterAuditRequest(
        String module,
        String entityType,
        Long entityId,
        String action,
        String user,
        String userRole,
        String source,
        String correlationId,
        String details
) {
}
