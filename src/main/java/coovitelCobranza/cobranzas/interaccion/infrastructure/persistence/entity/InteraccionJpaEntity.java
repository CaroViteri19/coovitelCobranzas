package coovitelCobranza.cobranzas.interaccion.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "interacciones")
public class InteraccionJpaEntity {

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
    public InteraccionJpaEntity() {
    }

    public InteraccionJpaEntity(Long id, Long casoGestionId, String canal, String plantilla,
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

    public String getPlantilla() {
        return plantilla;
    }

    public void setPlantilla(String plantilla) {
        this.plantilla = plantilla;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

