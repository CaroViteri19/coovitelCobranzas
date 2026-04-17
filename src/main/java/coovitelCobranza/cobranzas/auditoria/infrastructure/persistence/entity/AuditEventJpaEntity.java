package coovitelCobranza.cobranzas.auditoria.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_eventos")
public class AuditEventJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entidad", nullable = false, length = 80)
    private String entity;

    @Column(name = "entidad_id", nullable = false)
    private Long entityId;

    @Column(name = "accion", nullable = false, length = 80)
    private String action;

    @Column(name = "usuario", nullable = false, length = 80)
    private String user;

    @Column(name = "rol_usuario", length = 80)
    private String userRole;

    @Column(name = "origen", length = 30)
    private String source;

    @Column(name = "modulo", length = 50)
    private String module;

    @Column(name = "id_auditoria", length = 100)
    private String correlationId;

    @Column(name = "detalle", length = 1000)
    private String detail;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public AuditEventJpaEntity() {
    }

    public AuditEventJpaEntity(Long id,
                               String entity,
                               Long entityId,
                               String action,
                               String user,
                               String userRole,
                               String source,
                               String module,
                               String correlationId,
                               String detail,
                               LocalDateTime createdAt) {
        this.id = id;
        this.entity = entity;
        this.entityId = entityId;
        this.action = action;
        this.user = user;
        this.userRole = userRole;
        this.source = source;
        this.module = module;
        this.correlationId = correlationId;
        this.detail = detail;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
