package coovitelCobranza.cobranzas.auditoria.domain.repository;

import coovitelCobranza.cobranzas.auditoria.domain.model.AuditEvent;

import java.util.List;

public interface AuditEventRepository {

    AuditEvent save(AuditEvent evento);

    List<AuditEvent> findByEntidadAndEntidadIdOrderByCreatedAtDesc(String entity, Long entityId);
}

