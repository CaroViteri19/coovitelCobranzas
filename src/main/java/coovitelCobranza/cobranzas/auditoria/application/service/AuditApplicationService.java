package coovitelCobranza.cobranzas.auditoria.application.service;

import coovitelCobranza.cobranzas.auditoria.application.dto.AuditEventResponse;
import coovitelCobranza.cobranzas.auditoria.application.dto.RegisterAuditRequest;
import coovitelCobranza.cobranzas.auditoria.application.exception.AuditBusinessException;
import coovitelCobranza.cobranzas.auditoria.domain.model.AuditEvent;
import coovitelCobranza.cobranzas.auditoria.domain.repository.AuditEventRepository;
import coovitelCobranza.cobranzas.auditoria.domain.service.AuditService;
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
    public void register(RegisterAuditRequest request) {
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
    public List<AuditEventResponse> listByEntity(String entityType, Long entityId) {
        List<AuditEvent> events = repository.findByEntidadAndEntidadIdOrderByCreatedAtDesc(entityType, entityId);
        return events.stream().map(AuditEventResponse::fromDomain).toList();
    }

    // Backward-compatible wrappers during migration to English API.
    @Deprecated
    @Transactional
    public void registrar(RegisterAuditRequest request) {
        register(request);
    }

    @Deprecated
    @Transactional(readOnly = true)
    public List<AuditEventResponse> listarPorEntidad(String entity, Long entityId) {
        return listByEntity(entity, entityId);
    }
}
