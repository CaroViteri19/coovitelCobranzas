package coovitelCobranza.cobranzas.politicas.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "estrategias")
public class EstrategiaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false)
    private boolean activa;

    @Column(nullable = false)
    private int maxIntentosContacto;

    @Column(nullable = false)
    private int diasAntesDeeEscalacion;

    @Column(nullable = false, length = 100)
    private String rolAsignacionEscalada;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public EstrategiaJpaEntity() {}

    public EstrategiaJpaEntity(Long id, String nombre, String descripcion, boolean activa,
                              int maxIntentosContacto, int diasAntesDeeEscalacion,
                              String rolAsignacionEscalada, LocalDateTime updatedAt) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activa = activa;
        this.maxIntentosContacto = maxIntentosContacto;
        this.diasAntesDeeEscalacion = diasAntesDeeEscalacion;
        this.rolAsignacionEscalada = rolAsignacionEscalada;
        this.updatedAt = updatedAt;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
    public int getMaxIntentosContacto() { return maxIntentosContacto; }
    public void setMaxIntentosContacto(int maxIntentosContacto) { this.maxIntentosContacto = maxIntentosContacto; }
    public int getDiasAntesDeeEscalacion() { return diasAntesDeeEscalacion; }
    public void setDiasAntesDeeEscalacion(int diasAntesDeeEscalacion) { this.diasAntesDeeEscalacion = diasAntesDeeEscalacion; }
    public String getRolAsignacionEscalada() { return rolAsignacionEscalada; }
    public void setRolAsignacionEscalada(String rolAsignacionEscalada) { this.rolAsignacionEscalada = rolAsignacionEscalada; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

