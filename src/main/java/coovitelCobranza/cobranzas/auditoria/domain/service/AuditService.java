package coovitelCobranza.cobranzas.auditoria.domain.service;

public interface AuditService {

    void registerEvent(String module,
                       String entity,
                       Long entityId,
                       String action,
                       String user,
                       String userRole,
                       String source,
                       String details,
                       String correlationId);

    default void registrarEvent(String entity, Long entityId, String action, String user, String detail) {
        registerEvent("GENERAL", entity, entityId, action, user, null, "SYSTEM", detail, null);
    }
}
