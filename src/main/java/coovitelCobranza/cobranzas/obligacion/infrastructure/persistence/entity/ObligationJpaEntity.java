package coovitelCobranza.cobranzas.obligacion.infrastructure.persistence.entity;

import coovitelCobranza.cobranzas.obligacion.domain.model.Obligation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "obligation")
public class ObligationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "obligation_number")
    private String obligationNumber;

    @Column(name = "total_balance")
    private BigDecimal totalBalance;

    @Column(name = "overdue_balance")
    private BigDecimal overdueBalance;

    @Column(name = "overdue_days")
    private Integer overdueDays;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "status")
    private Integer status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Obligation toDomain() {
        return Obligation.reconstruct(
                id,
                customerId,
                obligationNumber,
                totalBalance,
                overdueBalance,
                overdueDays != null ? overdueDays : 0,
                mapStatus(status),
                dueDate,
                createdAt != null ? createdAt : LocalDateTime.now()
        );
    }

    public static ObligationJpaEntity fromDomain(Obligation obligacion) {
        ObligationJpaEntity entity = new ObligationJpaEntity();
        entity.id = obligacion.getId();
        entity.customerId = obligacion.getCustomerId();
        entity.obligationNumber = obligacion.getObligationNumber();
        entity.totalBalance = obligacion.getTotalBalance();
        entity.overdueBalance = obligacion.getOverdueBalance();
        entity.overdueDays = obligacion.getDelinquencyDays();
        entity.dueDate = obligacion.getDueDate();
        entity.status = mapStatus(obligacion.getStatus());
        entity.createdAt = obligacion.getUpdatedAt();
        return entity;
    }

    private static Obligation.StatusObligation mapStatus(Integer status) {
        if (status == null) {
            return Obligation.StatusObligation.AL_DIA;
        }
        return switch (status) {
            case 2 -> Obligation.StatusObligation.EN_MORA;
            case 3 -> Obligation.StatusObligation.REESTRUCTURADA;
            case 4 -> Obligation.StatusObligation.CANCELADA;
            default -> Obligation.StatusObligation.AL_DIA;
        };
    }

    private static Integer mapStatus(Obligation.StatusObligation estado) {
        return switch (estado) {
            case EN_MORA -> 2;
            case REESTRUCTURADA -> 3;
            case CANCELADA -> 4;
            default -> 1;
        };
    }
}

