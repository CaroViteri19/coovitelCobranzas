package coovitelCobranza.cobranzas.audit.application.service;

import coovitelCobranza.cobranzas.audit.application.dto.AuditEventResponse;
import coovitelCobranza.cobranzas.audit.application.dto.RegisterAuditRequest;
import coovitelCobranza.cobranzas.audit.application.exception.AuditBusinessException;
import coovitelCobranza.cobranzas.audit.domain.model.AuditEvent;
import coovitelCobranza.cobranzas.audit.domain.repository.AuditEventRepository;
import coovitelCobranza.cobranzas.audit.domain.service.AuditService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditApplicationService {

    private final AuditService auditService;
    private final AuditEventRepository repository;

    public AuditApplicationService(AuditService auditService,
                                   AuditEventRepository repository) {
        this.auditService = auditService;
        this.repository = repository;
    }

    @Transactional
    public void registerEvent(RegisterAuditRequest request) {
        try {
            auditService.registerEvent(
                    request.module() != null ? request.module() : "GENERAL",
                    request.entityType(),
                    request.entityId(),
                    request.action(),
                    request.user(),
                    request.userRole(),
                    request.source() != null ? request.source() : "SYSTEM",
                    request.details(),
                    request.correlationId()
            );
        } catch (Exception e) {
            throw new AuditBusinessException("Error registering audit event", e);
        }
    }

    @Transactional(readOnly = true)
    public List<AuditEventResponse> listEventsByEntity(String entityType, Long entityId) {
        List<AuditEvent> events = repository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId);
        return events.stream().map(AuditEventResponse::fromDomain).toList();
    }

    // Backward-compatible wrappers during migration to English API.
    @Deprecated
    @Transactional
    public void register(RegisterAuditRequest request) {
        registerEvent(request);
    }

    @Deprecated
    @Transactional(readOnly = true)
    public List<AuditEventResponse> listByEntity(String entityType, Long entityId) {
        return listEventsByEntity(entityType, entityId);
    }

    public List<AuditEventResponse> listEventsByEntityTypeAndId(String entityType, Long entityId) {
        return listEventsByEntity(entityType, entityId);
    }
}
