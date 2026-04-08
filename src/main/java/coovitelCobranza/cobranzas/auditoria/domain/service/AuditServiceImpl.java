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
    public void registrarEvent(String entity, Long entityId, String action, String user, String detail) {
        AuditEvent evento = AuditEvent.crear(entity, entityId, action, user, detail);
        repository.save(evento);
    }
}

