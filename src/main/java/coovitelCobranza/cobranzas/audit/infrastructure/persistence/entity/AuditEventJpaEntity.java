package coovitelCobranza.cobranzas.audit.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_events")
public class AuditEventJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_type", nullable = false, length = 80)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "action", nullable = false, length = 80)
    private String action;

    @Column(name = "username", nullable = false, length = 80)
    private String username;

    @Column(name = "user_role", length = 80)
    private String userRole;

    @Column(name = "source", length = 30)
    private String source;

    @Column(name = "module_name", length = 50)
    private String module;

    @Column(name = "correlation_id", length = 100)
    private String correlationId;

    @Column(name = "details", length = 1000)
    private String details;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public AuditEventJpaEntity() {
    }

    public AuditEventJpaEntity(Long id,
                               String entityType,
                               Long entityId,
                               String action,
                               String username,
                               String userRole,
                               String source,
                               String module,
                               String correlationId,
                               String details,
                               LocalDateTime createdAt) {
        this.id = id;
        this.entityType = entityType;
        this.entityId = entityId;
        this.action = action;
        this.username = username;
        this.userRole = userRole;
        this.source = source;
        this.module = module;
        this.correlationId = correlationId;
        this.details = details;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
