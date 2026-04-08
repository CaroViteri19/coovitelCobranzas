package coovitelCobranza.cobranzas.politicas.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "estrategias")
public class StrategyJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private boolean activa;

    @Column(nullable = false)
    private int maxContactAttempts;

    @Column(nullable = false)
    private int daysBeforeEscalation;

    @Column(nullable = false, length = 100)
    private String escalationAssignmentRole;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public StrategyJpaEntity() {}

    public StrategyJpaEntity(Long id, String name, String description, boolean activa,
                              int maxContactAttempts, int daysBeforeEscalation,
                              String escalationAssignmentRole, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.activa = activa;
        this.maxContactAttempts = maxContactAttempts;
        this.daysBeforeEscalation = daysBeforeEscalation;
        this.escalationAssignmentRole = escalationAssignmentRole;
        this.updatedAt = updatedAt;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return name; }
    public void setNombre(String name) { this.name = name; }
    public String getDescripcion() { return description; }
    public void setDescripcion(String description) { this.description = description; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
    public int getMaxIntentosContact() { return maxContactAttempts; }
    public void setMaxIntentosContact(int maxContactAttempts) { this.maxContactAttempts = maxContactAttempts; }
    public int getDiasAntesDeeEscalacion() { return daysBeforeEscalation; }
    public void setDiasAntesDeeEscalacion(int daysBeforeEscalation) { this.daysBeforeEscalation = daysBeforeEscalation; }
    public String getRolAsignacionEscalada() { return escalationAssignmentRole; }
    public void setRolAsignacionEscalada(String escalationAssignmentRole) { this.escalationAssignmentRole = escalationAssignmentRole; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

