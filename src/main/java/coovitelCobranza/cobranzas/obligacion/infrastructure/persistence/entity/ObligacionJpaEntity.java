package coovitelCobranza.cobranzas.obligacion.infrastructure.persistence;

import coovitelCobranza.cobranzas.obligacion.domain.model.Obligacion;
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
public class ObligacionJpaEntity {

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

    public Obligacion toDomain() {
        return Obligacion.reconstruir(
                id,
                customerId,
                obligationNumber,
                totalBalance,
                overdueBalance,
                overdueDays != null ? overdueDays : 0,
                mapEstado(status),
                dueDate,
                createdAt != null ? createdAt : LocalDateTime.now()
        );
    }

    public static ObligacionJpaEntity fromDomain(Obligacion obligacion) {
        ObligacionJpaEntity entity = new ObligacionJpaEntity();
        entity.id = obligacion.getId();
        entity.customerId = obligacion.getClienteId();
        entity.obligationNumber = obligacion.getNumeroObligacion();
        entity.totalBalance = obligacion.getSaldoTotal();
        entity.overdueBalance = obligacion.getSaldoVencido();
        entity.overdueDays = obligacion.getDiasMora();
        entity.dueDate = obligacion.getFechaVencimiento();
        entity.status = mapEstado(obligacion.getEstado());
        entity.createdAt = obligacion.getUpdatedAt();
        return entity;
    }

    private static Obligacion.EstadoObligacion mapEstado(Integer status) {
        if (status == null) {
            return Obligacion.EstadoObligacion.AL_DIA;
        }
        return switch (status) {
            case 2 -> Obligacion.EstadoObligacion.EN_MORA;
            case 3 -> Obligacion.EstadoObligacion.REESTRUCTURADA;
            case 4 -> Obligacion.EstadoObligacion.CANCELADA;
            default -> Obligacion.EstadoObligacion.AL_DIA;
        };
    }

    private static Integer mapEstado(Obligacion.EstadoObligacion estado) {
        return switch (estado) {
            case EN_MORA -> 2;
            case REESTRUCTURADA -> 3;
            case CANCELADA -> 4;
            default -> 1;
        };
    }
}

