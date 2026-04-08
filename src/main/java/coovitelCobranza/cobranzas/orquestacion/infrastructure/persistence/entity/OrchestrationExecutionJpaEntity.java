package coovitelCobranza.cobranzas.orquestacion.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orquestacion_ejecuciones")
public class OrchestrationExecutionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long casoGestionId;

    @Column(nullable = false, length = 20)
    private String canal;

    @Column(nullable = false, length = 120)
    private String destino;

    @Column(nullable = false, length = 500)
    private String plantilla;

    @Column(nullable = false, length = 20)
    private String estado;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public OrchestrationExecutionJpaEntity() {
    }

    public OrchestrationExecutionJpaEntity(Long id, Long casoGestionId, String canal, String destino,
                                          String plantilla, String estado, LocalDateTime createdAt) {
        this.id = id;
        this.casoGestionId = casoGestionId;
        this.canal = canal;
        this.destino = destino;
        this.plantilla = plantilla;
        this.estado = estado;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCaseId() {
        return casoGestionId;
    }

    public void setCaseId(Long casoGestionId) {
        this.casoGestionId = casoGestionId;
    }

    public String getChannel() {
        return canal;
    }

    public void setChannel(String canal) {
        this.canal = canal;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getTemplate() {
        return plantilla;
    }

    public void setPlantilla(String plantilla) {
        this.plantilla = plantilla;
    }

    public String getStatus() {
        return estado;
    }

    public void setStatus(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

