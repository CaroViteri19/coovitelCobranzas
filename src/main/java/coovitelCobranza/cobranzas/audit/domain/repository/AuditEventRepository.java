package coovitelCobranza.cobranzas.audit.domain.repository;

import coovitelCobranza.cobranzas.audit.domain.model.AuditEvent;

import java.util.List;

public interface AuditEventRepository {

    AuditEvent save(AuditEvent event);

    List<AuditEvent> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(String entityType, Long entityId);
}

