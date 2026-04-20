package coovitelCobranza.cobranzas.audit.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain model for an audit event.
 *
 * <p>Immutable: once created it cannot be modified. It is built through
 * {@link #create} (for new events, still without a persisted ID) or
 * {@link #reconstruct} (to rehydrate from the persistence layer).
 */
public class AuditEvent {

    private final Long id;
    private final String entityType;
    private final Long entityId;
    private final String action;
    private final String user;
    private final String userRole;
    private final String source;
    private final String module;
    private final String correlationId;
    private final String details;
    private final LocalDateTime createdAt;

    private AuditEvent(Long id,
                       String entityType,
                       Long entityId,
                       String action,
                       String user,
                       String userRole,
                       String source,
                       String module,
                       String correlationId,
                       String details,
                       LocalDateTime createdAt) {
        this.id = id;
        this.entityType = Objects.requireNonNull(entityType, "entityType is required");
        this.entityId = Objects.requireNonNull(entityId, "entityId is required");
        this.action = Objects.requireNonNull(action, "action is required");
        this.user = Objects.requireNonNull(user, "user is required");
        this.userRole = userRole;
        this.source = source;
        this.module = module;
        this.correlationId = correlationId;
        this.details = details;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    /**
     * Main factory for creating a new event (without a persisted ID).
     */
    public static AuditEvent create(String entityType,
                                    Long entityId,
                                    String action,
                                    String user,
                                    String userRole,
                                    String source,
                                    String module,
                                    String correlationId,
                                    String details) {
        return new AuditEvent(
                null,
                entityType,
                entityId,
                action,
                user,
                userRole,
                source,
                module,
                correlationId,
                details,
                LocalDateTime.now()
        );
    }

    /**
     * Rehydrates an event from the persistence layer.
     */
    public static AuditEvent reconstruct(Long id,
                                         String entityType,
                                         Long entityId,
                                         String action,
                                         String user,
                                         String userRole,
                                         String source,
                                         String module,
                                         String correlationId,
                                         String details,
                                         LocalDateTime createdAt) {
        return new AuditEvent(id, entityType, entityId, action, user, userRole, source, module, correlationId, details, createdAt);
    }

    public Long getId() { return id; }
    public String getEntityType() { return entityType; }
    public Long getEntityId() { return entityId; }
    public String getAction() { return action; }
    public String getUser() { return user; }
    public String getUserRole() { return userRole; }
    public String getSource() { return source; }
    public String getModule() { return module; }
    public String getCorrelationId() { return correlationId; }
    public String getDetails() { return details; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
