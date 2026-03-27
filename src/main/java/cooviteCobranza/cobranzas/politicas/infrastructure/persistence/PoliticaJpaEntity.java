package cooviteCobranza.cobranzas.politicas.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "politicas")
public class PoliticaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long estrategiaId;

    @Column(nullable = false, length = 30)
    private String tipoCobro;

    @Column(nullable = false)
    private int diasMoraMinimo;

    @Column(nullable = false)
    private int diasMoraMaximo;

    @Column(nullable = false, length = 200)
    private String accion;

    @Column(nullable = false)
    private boolean activa;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public PoliticaJpaEntity() {}

    public PoliticaJpaEntity(Long id, Long estrategiaId, String tipoCobro, int diasMoraMinimo,
                            int diasMoraMaximo, String accion, boolean activa, LocalDateTime updatedAt) {
        this.id = id;
        this.estrategiaId = estrategiaId;
        this.tipoCobro = tipoCobro;
        this.diasMoraMinimo = diasMoraMinimo;
        this.diasMoraMaximo = diasMoraMaximo;
        this.accion = accion;
        this.activa = activa;
        this.updatedAt = updatedAt;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEstrategiaId() { return estrategiaId; }
    public void setEstrategiaId(Long estrategiaId) { this.estrategiaId = estrategiaId; }
    public String getTipoCobro() { return tipoCobro; }
    public void setTipoCobro(String tipoCobro) { this.tipoCobro = tipoCobro; }
    public int getDiasMoraMinimo() { return diasMoraMinimo; }
    public void setDiasMoraMinimo(int diasMoraMinimo) { this.diasMoraMinimo = diasMoraMinimo; }
    public int getDiasMoraMaximo() { return diasMoraMaximo; }
    public void setDiasMoraMaximo(int diasMoraMaximo) { this.diasMoraMaximo = diasMoraMaximo; }
    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

