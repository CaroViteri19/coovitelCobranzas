package coovitelCobranza.cobranzas.audit.domain.service;

import coovitelCobranza.cobranzas.audit.domain.context.AuditContext;
import coovitelCobranza.cobranzas.audit.domain.model.AuditEvent;
import coovitelCobranza.cobranzas.audit.domain.repository.AuditEventRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Audit service implementation.
 *
 * <p>Each call to {@link #registerEvent} runs in its own transaction
 * ({@code REQUIRES_NEW}), independent from the caller transaction. This
 * guarantees that audit events (including failures) are persisted even if
 * the main transaction rolls back.
 */
@Component
public class AuditServiceImpl implements AuditService {

    private final AuditEventRepository repository;

    public AuditServiceImpl(AuditEventRepository repository) {
        this.repository = repository;
    }

    /**
     * Registers an audit event in an independent transaction.
     *
     * <p>Using {@code Propagation.REQUIRES_NEW}, this method suspends the
     * caller's active transaction, opens a new transaction, persists the event
     * and commits it before returning control to the caller. If the caller later
     * rolls back, the audit event has already been saved.
     *
     * <p>If {@code correlationId} is {@code null}, the method attempts to
     * recover it from the current thread {@link AuditContext} as a fallback.
     *
     * @param module        system module that generates the event (for example, "INTEGRATION")
     * @param entityType    affected entity type (for example, "BULK_UPLOAD", "USER")
     * @param entityId      entity ID (may be null when not applicable)
     * @param action        performed action (for example, "UPLOAD_STARTED", "UPLOAD_FAILED")
     * @param user          user performing the action
     * @param userRole      user role
     * @param source        request source (for example, "WEB", "API")
     * @param details       detailed event description (max 1000 characters)
     * @param correlationId correlation ID; if null, it is taken from {@link AuditContext}
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registerEvent(String module,
                              String entityType,
                              Long entityId,
                              String action,
                              String user,
                              String userRole,
                              String source,
                              String details,
                              String correlationId) {
        // 0L as a sentinel when there is no known entity
        // (for example, a failed login for a non-existent user).
        Long safeEntityId = entityId != null ? entityId : 0L;

        // If the caller does not pass a correlationId, use the current thread value.
        String effectiveCorrelationId = correlationId != null
                ? correlationId
                : AuditContext.getCorrelationIdAsString();

        AuditEvent event = AuditEvent.create(
                entityType,
                safeEntityId,
                action,
                user,
                userRole,
                source,
                module,
                effectiveCorrelationId,
                details
        );
        repository.save(event);
    }

    /**
     * Sobrecarga que recupera el {@code correlationId} automáticamente desde
     * {@link AuditContext}. Delega en la firma canónica.
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registerEvent(String module,
                              String entity,
                              Long entityId,
                              String action,
                              String user,
                              String userRole,
                              String source,
                              String details) {
        registerEvent(
                module, entity, entityId, action, user, userRole, source, details,
                AuditContext.getCorrelationIdAsString()
        );
    }
}
