package coovitelCobranza.cobranzas.policies.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "policies")
public class PolicyJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long strategyId;

    @Column(nullable = false, length = 30)
    private String collectionType;

    @Column(nullable = false)
    private int minDelinquencyDays;

    @Column(nullable = false)
    private int maxDelinquencyDays;

    @Column(nullable = false, length = 200)
    private String action;

    @Column(name = "active", nullable = false)
    private boolean activa;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public PolicyJpaEntity() {}

    public PolicyJpaEntity(Long id, Long strategyId, String collectionType, int minDelinquencyDays,
                            int maxDelinquencyDays, String action, boolean activa, LocalDateTime updatedAt) {
        this.id = id;
        this.strategyId = strategyId;
        this.collectionType = collectionType;
        this.minDelinquencyDays = minDelinquencyDays;
        this.maxDelinquencyDays = maxDelinquencyDays;
        this.action = action;
        this.activa = activa;
        this.updatedAt = updatedAt;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStrategyId() { return strategyId; }
    public void setStrategyId(Long strategyId) { this.strategyId = strategyId; }
    public String getCollectionType() { return collectionType; }
    public void setCollectionType(String collectionType) { this.collectionType = collectionType; }
    public int getDiasDelinquencyMinimo() { return minDelinquencyDays; }
    public void setDiasDelinquencyMinimo(int minDelinquencyDays) { this.minDelinquencyDays = minDelinquencyDays; }
    public int getDiasDelinquencyMaximo() { return maxDelinquencyDays; }
    public void setDiasDelinquencyMaximo(int maxDelinquencyDays) { this.maxDelinquencyDays = maxDelinquencyDays; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

