package coovitelCobranza.cobranzas.auditoria.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class AuditEvent {

    private final Long id;
    private final String entityType;
    private final Long entityId;
    private final String action;
    private final String user;
    private final String details;
    private final LocalDateTime createdAt;

    private AuditEvent(Long id,
                       String entityType,
                       Long entityId,
                       String action,
                       String user,
                       String details,
                       LocalDateTime createdAt) {
        this.id = id;
        this.entityType = Objects.requireNonNull(entityType, "entityType is required");
        this.entityId = Objects.requireNonNull(entityId, "entityId is required");
        this.action = Objects.requireNonNull(action, "action is required");
        this.user = Objects.requireNonNull(user, "user is required");
        this.details = details;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public static AuditEvent create(String entityType, Long entityId, String action, String user, String details) {
        return new AuditEvent(null, entityType, entityId, action, user, details, LocalDateTime.now());
    }

    public static AuditEvent crear(String entityType, Long entityId, String action, String user, String details) {
        return create(entityType, entityId, action, user, details);
    }

    public static AuditEvent reconstruct(Long id,
                                         String entityType,
                                         Long entityId,
                                         String action,
                                         String user,
                                         String details,
                                         LocalDateTime createdAt) {
        return new AuditEvent(id, entityType, entityId, action, user, details, createdAt);
    }

    public Long getId() { return id; }
    public String getEntityType() { return entityType; }
    public String getEntidad() { return entityType; }
    public Long getEntityId() { return entityId; }
    public Long getEntidadId() { return entityId; }
    public String getAction() { return action; }
    public String getUser() { return user; }
    public String getUsuario() { return user; }
    public String getDetails() { return details; }
    public String getDetalle() { return details; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
