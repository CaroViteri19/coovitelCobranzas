package coovitelCobranza.cobranzas.obligation.infrastructure.persistence.entity;

import coovitelCobranza.cobranzas.obligation.domain.model.Obligation;
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

    @Column(name = "segment", length = 30)
    private String segment;

    @Column(name = "product", length = 50)
    private String product;

    @Column(name = "agent_code", length = 10)
    private String agentCode;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Batch fields getters/setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getObligationNumber() { return obligationNumber; }
    public void setObligationNumber(String obligationNumber) { this.obligationNumber = obligationNumber; }

    public java.math.BigDecimal getTotalBalance() { return totalBalance; }
    public void setTotalBalance(java.math.BigDecimal totalBalance) { this.totalBalance = totalBalance; }

    public java.math.BigDecimal getOverdueBalance() { return overdueBalance; }
    public void setOverdueBalance(java.math.BigDecimal overdueBalance) { this.overdueBalance = overdueBalance; }

    public Integer getOverdueDays() { return overdueDays; }
    public void setOverdueDays(Integer overdueDays) { this.overdueDays = overdueDays; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getSegment() { return segment; }
    public void setSegment(String segment) { this.segment = segment; }

    public String getProduct() { return product; }
    public void setProduct(String product) { this.product = product; }

    public String getAgentCode() { return agentCode; }
    public void setAgentCode(String agentCode) { this.agentCode = agentCode; }

    // Backward-compatible aliases during migration
    public String getSegmento() { return getSegment(); }
    public void setSegmento(String segmento) { setSegment(segmento); }
    public String getProducto() { return getProduct(); }
    public void setProducto(String producto) { setProduct(producto); }
    public String getCodigoAgente() { return getAgentCode(); }
    public void setCodigoAgente(String codigoAgente) { setAgentCode(codigoAgente); }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Domain mapping

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

    public static ObligationJpaEntity fromDomain(Obligation obligation) {
        ObligationJpaEntity entity = new ObligationJpaEntity();
        entity.id = obligation.getId();
        entity.customerId = obligation.getCustomerId();
        entity.obligationNumber = obligation.getObligationNumber();
        entity.totalBalance = obligation.getTotalBalance();
        entity.overdueBalance = obligation.getOverdueBalance();
        entity.overdueDays = obligation.getDelinquencyDays();
        entity.dueDate = obligation.getDueDate();
        entity.status = mapStatus(obligation.getStatus());
        entity.createdAt = obligation.getUpdatedAt();
        // Batch import fields
        entity.segment = obligation.getSegment();
        entity.product = obligation.getProduct();
        entity.agentCode = obligation.getAgentCode();
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

