package coovitelCobranza.cobranzas.auditoria.domain.service;

public interface AuditService {

    void registrarEvent(String entity, Long entityId, String action, String user, String detail);
}

