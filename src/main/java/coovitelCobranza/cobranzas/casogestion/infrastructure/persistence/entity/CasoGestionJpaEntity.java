package coovitelCobranza.cobranzas.casogestion.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "casos_gestion")
public class CasoGestionJpaEntity {

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
    private LocalDateTime proximaAccionAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Constructores
    public CasoGestionJpaEntity() {
    }

    public CasoGestionJpaEntity(Long id, Long obligacionId, String prioridad, String estado,
                               String asesorAsignado, LocalDateTime proximaAccionAt, LocalDateTime updatedAt) {
        this.id = id;
        this.obligacionId = obligacionId;
        this.prioridad = prioridad;
        this.estado = estado;
        this.asesorAsignado = asesorAsignado;
        this.proximaAccionAt = proximaAccionAt;
        this.updatedAt = updatedAt;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getObligacionId() {
        return obligacionId;
    }

    public void setObligacionId(Long obligacionId) {
        this.obligacionId = obligacionId;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getAsesorAsignado() {
        return asesorAsignado;
    }

    public void setAsesorAsignado(String asesorAsignado) {
        this.asesorAsignado = asesorAsignado;
    }

    public LocalDateTime getProximaAccionAt() {
        return proximaAccionAt;
    }

    public void setProximaAccionAt(LocalDateTime proximaAccionAt) {
        this.proximaAccionAt = proximaAccionAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

