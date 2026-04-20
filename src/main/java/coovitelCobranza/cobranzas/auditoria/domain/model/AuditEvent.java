package coovitelCobranza.cobranzas.auditoria.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Modelo de dominio para un evento de auditoría.
 *
 * <p>Inmutable: una vez creado no se puede modificar. Se construye a través de
 * {@link #create} (para eventos nuevos, aún sin ID persistido) o
 * {@link #reconstruct} (para rehidratar desde la capa de persistencia).
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
     * Factoría principal para crear un nuevo evento (sin ID persistido).
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
     * Rehidrata un evento desde la capa de persistencia.
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
