package cooviteCobranza.cobranzas.orquestacion.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orquestacion_ejecuciones")
public class OrquestacionEjecucionJpaEntity {

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

    public OrquestacionEjecucionJpaEntity() {
    }

    public OrquestacionEjecucionJpaEntity(Long id, Long casoGestionId, String canal, String destino,
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

    public Long getCasoGestionId() {
        return casoGestionId;
    }

    public void setCasoGestionId(Long casoGestionId) {
        this.casoGestionId = casoGestionId;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getPlantilla() {
        return plantilla;
    }

    public void setPlantilla(String plantilla) {
        this.plantilla = plantilla;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

