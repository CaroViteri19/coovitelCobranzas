package coovitelCobranza.cobranzas.interaccion.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "interactiones")
public class InteractionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long casoGestionId;

    @Column(nullable = false, length = 20)
    private String canal;

    @Column(length = 500)
    private String plantilla;

    @Column(nullable = false, length = 20)
    private String resultado;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Constructores
    public InteractionJpaEntity() {
    }

    public InteractionJpaEntity(Long id, Long casoGestionId, String canal, String plantilla,
                               String resultado, LocalDateTime createdAt) {
        this.id = id;
        this.casoGestionId = casoGestionId;
        this.canal = canal;
        this.plantilla = plantilla;
        this.resultado = resultado;
        this.createdAt = createdAt;
    }

    // Getters y Setters
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

    public String getTemplate() {
        return plantilla;
    }

    public void setPlantilla(String plantilla) {
        this.plantilla = plantilla;
    }

    public String getResultStatus() {
        return resultado;
    }

    public void setResult(String resultado) {
        this.resultado = resultado;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

