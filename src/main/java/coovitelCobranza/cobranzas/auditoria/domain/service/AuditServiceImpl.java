package coovitelCobranza.cobranzas.auditoria.domain.service;

import coovitelCobranza.cobranzas.auditoria.domain.model.AuditEvent;
import coovitelCobranza.cobranzas.auditoria.domain.repository.AuditEventRepository;
import org.springframework.stereotype.Component;

@Component
public class AuditServiceImpl implements AuditService {

    private final AuditEventRepository repository;

    public AuditServiceImpl(AuditEventRepository repository) {
        this.repository = repository;
    }

    @Override
    public void registerEvent(String module,
                              String entity,
                              Long entityId,
                              String action,
                              String user,
                              String userRole,
                              String source,
                              String details,
                              String correlationId) {
        // 0L como centinela cuando no hay entidad conocida (ej: login fallido de usuario inexistente)
        Long safeEntityId = entityId != null ? entityId : 0L;

        AuditEvent event = AuditEvent.create(
                entity,
                safeEntityId,
                action,
                user,
                userRole,
                source,
                module,
                correlationId,
                details
        );
        repository.save(event);
    }
}
