package coovitelCobranza.cobranzas.auditoria.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_eventos")
public class AuditEventJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String entity;

    @Column(nullable = false)
    private Long entityId;

    @Column(nullable = false, length = 80)
    private String action;

    @Column(nullable = false, length = 80)
    private String user;

    @Column(length = 500)
    private String detail;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public AuditEventJpaEntity() {
    }

    public AuditEventJpaEntity(Long id, String entity, Long entityId, String action,
                                    String user, String detail, LocalDateTime createdAt) {
        this.id = id;
        this.entity = entity;
        this.entityId = entityId;
        this.action = action;
        this.user = user;
        this.detail = detail;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntidad() {
        return entity;
    }

    public void setEntidad(String entity) {
        this.entity = entity;
    }

    public Long getEntidadId() {
        return entityId;
    }

    public void setEntidadId(Long entityId) {
        this.entityId = entityId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUsuario() {
        return user;
    }

    public void setUsuario(String user) {
        this.user = user;
    }

    public String getDetalle() {
        return detail;
    }

    public void setDetalle(String detail) {
        this.detail = detail;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

