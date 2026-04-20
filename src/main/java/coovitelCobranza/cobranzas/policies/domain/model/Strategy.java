package coovitelCobranza.cobranzas.policies.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Strategy {

    private final Long id;
    private final String name;
    private final String description;
    private boolean active;
    private int maxContactAttempts;
    private int daysBeforeEscalation;
    private String escalationAssignmentRole;
    private LocalDateTime updatedAt;

    private Strategy(Long id,
                       String name,
                       String description,
                       boolean active,
                       int maxContactAttempts,
                       int daysBeforeEscalation,
                       String escalationAssignmentRole) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "name es requerido");
        this.description = description;
        this.active = active;
        this.maxContactAttempts = maxContactAttempts;
        this.daysBeforeEscalation = daysBeforeEscalation;
        this.escalationAssignmentRole = escalationAssignmentRole;
        this.updatedAt = LocalDateTime.now();
    }

    public static Strategy create(String name, String description) {
        return new Strategy(null, name, description, true, 3, 5, "SUPERVISOR");
    }

    public static Strategy crear(String name, String description) {
        return create(name, description);
    }

    public static Strategy reconstruct(Long id,
                                        String name,
                                        String description,
                                        boolean activa,
                                        int maxContactAttempts,
                                        int daysBeforeEscalation,
                                        String escalationAssignmentRole,
                                        LocalDateTime updatedAt) {
        Strategy estrategia = new Strategy(id, name, description, activa,
                maxContactAttempts, daysBeforeEscalation, escalationAssignmentRole);
        estrategia.updatedAt = updatedAt;
        return estrategia;
    }

    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void activar() {
        activate();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void desactivar() {
        deactivate();
    }

    public void configureParameters(int maxAttempts, int escalationDays, String escalationRole) {
        if (maxAttempts <= 0) {
            throw new IllegalArgumentException("maxAttempts must be greater than 0");
        }
        if (escalationDays < 0) {
            throw new IllegalArgumentException("escalationDays cannot be negative");
        }
        this.maxContactAttempts = maxAttempts;
        this.daysBeforeEscalation = escalationDays;
        this.escalationAssignmentRole = Objects.requireNonNull(escalationRole, "escalationRole is required");
        this.updatedAt = LocalDateTime.now();
    }

    public void configurarParametros(int maxIntentos, int diasEscalacion, String rolEscalacion) {
        configureParameters(maxIntentos, diasEscalacion, rolEscalacion);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNombre() {
        return getName();
    }

    public String getDescription() {
        return description;
    }

    public String getDescripcion() {
        return getDescription();
    }

    public boolean isActive() {
        return active;
    }

    public boolean isActiva() {
        return active;
    }

    public int getMaxContactAttempts() {
        return maxContactAttempts;
    }

    public int getMaxIntentosContact() {
        return getMaxContactAttempts();
    }

    public int getDaysBeforeEscalation() {
        return daysBeforeEscalation;
    }

    public int getDiasAntesDeeEscalacion() {
        return getDaysBeforeEscalation();
    }

    public String getEscalationAssignmentRole() {
        return escalationAssignmentRole;
    }

    public String getRolAsignacionEscalada() {
        return getEscalationAssignmentRole();
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
