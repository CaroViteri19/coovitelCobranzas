package coovitelCobranza.cobranzas.policies.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Policy {

    private final Long id;
    private final Long strategyId;
    private final CollectionType collectionType;
    private int minDelinquencyDays;
    private int maxDelinquencyDays;
    private String action;
    private boolean activa;
    private LocalDateTime updatedAt;

    public enum CollectionType {
        PROACTIVO,      // Contact antes de mora
        REACTIVO,       // Después de vencer
        COBRANZA_LEGAL, // Escalado a abogado
        NEGOCIACION     // Planes de pago
    }

    private Policy(Long id,
                   Long strategyId,
                   CollectionType collectionType,
                   int minDelinquencyDays,
                   int maxDelinquencyDays,
                   String action,
                   boolean activa) {
        this.id = id;
        this.strategyId = Objects.requireNonNull(strategyId, "strategyId is required");
        this.collectionType = Objects.requireNonNull(collectionType, "collectionType is required");
        this.minDelinquencyDays = minDelinquencyDays;
        this.maxDelinquencyDays = maxDelinquencyDays;
        this.action = Objects.requireNonNull(action, "action is required");
        this.activa = activa;
        this.updatedAt = LocalDateTime.now();
    }

    public static Policy create(Long strategyId, CollectionType collectionType, String action) {
        return new Policy(null, strategyId, collectionType, 0, 30, action, true);
    }

    public static Policy crear(Long strategyId, CollectionType collectionType, String action) {
        return create(strategyId, collectionType, action);
    }

    public static Policy reconstruct(Long id,
                                     Long strategyId,
                                     CollectionType collectionType,
                                     int minDelinquencyDays,
                                     int maxDelinquencyDays,
                                     String action,
                                     boolean activa,
                                     LocalDateTime updatedAt) {
        Policy policy = new Policy(id, strategyId, collectionType, minDelinquencyDays,
                maxDelinquencyDays, action, activa);
        policy.updatedAt = updatedAt;
        return policy;
    }

    public static Policy reconstruct(Long id,
                                     Long strategyId,
                                     CollectionType collectionType,
                                     int minDelinquencyDays,
                                     int maxDelinquencyDays,
                                     String action,
                                     boolean activa,
                                     java.time.LocalDateTime updatedAt,
                                     boolean ignored) {
        return reconstruct(id, strategyId, collectionType, minDelinquencyDays, maxDelinquencyDays, action, activa, updatedAt);
    }

    public void activate() {
        this.activa = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.activa = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void activar() { activate(); }
    public void desactivar() { deactivate(); }

    public void configureDelinquencyRange(int min, int max) {
        if (min < 0 || max < 0) {
            throw new IllegalArgumentException("Days cannot be negative");
        }
        if (min > max) {
            throw new IllegalArgumentException("Minimum cannot be greater than maximum");
        }
        this.minDelinquencyDays = min;
        this.maxDelinquencyDays = max;
        this.updatedAt = LocalDateTime.now();
    }

    public void configurarRangoDelinquency(int min, int max) { configureDelinquencyRange(min, max); }

    public boolean appliesTo(int delinquencyDays) {
        return delinquencyDays >= minDelinquencyDays && delinquencyDays <= maxDelinquencyDays;
    }

    public boolean aplicaA(int diasDelinquency) { return appliesTo(diasDelinquency); }

    public Long getId() { return id; }
    public Long getStrategyId() { return strategyId; }
    public CollectionType getCollectionType() { return collectionType; }
    public CollectionType getTipoCobro() { return collectionType; }
    public int getMinDelinquencyDays() { return minDelinquencyDays; }
    public int getDiasDelinquencyMinimo() { return minDelinquencyDays; }
    public int getMaxDelinquencyDays() { return maxDelinquencyDays; }
    public int getDiasDelinquencyMaximo() { return maxDelinquencyDays; }
    public String getAction() { return action; }
    public boolean isActive() { return activa; }
    public boolean isActiva() { return activa; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
