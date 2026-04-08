package coovitelCobranza.cobranzas.casogestion.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "casos_gestion")
public class CaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long obligacionId;

    @Column(nullable = false, length = 20)
    private String prioridad;

    @Column(nullable = false, length = 20)
    private String estado;

    @Column(length = 100)
    private String asesorAsignado;

    @Column
    private LocalDateTime proximaActionAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Constructores
    public CaseJpaEntity() {
    }

    public CaseJpaEntity(Long id, Long obligacionId, String prioridad, String estado,
                               String asesorAsignado, LocalDateTime proximaActionAt, LocalDateTime updatedAt) {
        this.id = id;
        this.obligacionId = obligacionId;
        this.prioridad = prioridad;
        this.estado = estado;
        this.asesorAsignado = asesorAsignado;
        this.proximaActionAt = proximaActionAt;
        this.updatedAt = updatedAt;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getObligationId() {
        return obligacionId;
    }

    public void setObligationId(Long obligacionId) {
        this.obligacionId = obligacionId;
    }

    public String getPriority() {
        return prioridad;
    }

    public void setPriority(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getStatus() {
        return estado;
    }

    public void setStatus(String estado) {
        this.estado = estado;
    }

    public String getAssignedAdvisor() {
        return asesorAsignado;
    }

    public void setAdvisorAsignado(String asesorAsignado) {
        this.asesorAsignado = asesorAsignado;
    }

    public LocalDateTime getNextActionAt() {
        return proximaActionAt;
    }

    public void setProximaActionAt(LocalDateTime proximaActionAt) {
        this.proximaActionAt = proximaActionAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

