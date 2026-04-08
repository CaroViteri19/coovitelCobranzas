package coovitelCobranza.cobranzas.obligacion.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Obligation {

    private final Long id;
    private final Long customerId;
    private final String obligationNumber;
    private BigDecimal totalBalance;
    private BigDecimal overdueBalance;
    private int delinquencyDays;
    private StatusObligation status;
    private LocalDate dueDate;
    private LocalDateTime updatedAt;

    private Obligation(Long id,
                       Long customerId,
                       String obligationNumber,
                       BigDecimal totalBalance,
                       BigDecimal overdueBalance,
                       int delinquencyDays,
                       StatusObligation status,
                       LocalDate dueDate) {
        this.id = id;
        this.customerId = Objects.requireNonNull(customerId, "customerId is required");
        this.obligationNumber = Objects.requireNonNull(obligationNumber, "obligationNumber is required");
        this.totalBalance = Objects.requireNonNull(totalBalance, "totalBalance is required");
        this.overdueBalance = Objects.requireNonNull(overdueBalance, "overdueBalance is required");
        this.delinquencyDays = delinquencyDays;
        this.status = Objects.requireNonNull(status, "status is required");
        this.dueDate = dueDate;
        this.updatedAt = LocalDateTime.now();
    }

    public static Obligation create(Long customerId, String obligationNumber, BigDecimal totalBalance) {
        return new Obligation(null, customerId, obligationNumber, totalBalance, BigDecimal.ZERO, 0,
                StatusObligation.AL_DIA, null);
    }

    public static Obligation reconstruct(Long id,
                                         Long customerId,
                                         String obligationNumber,
                                         BigDecimal totalBalance,
                                         BigDecimal overdueBalance,
                                         int delinquencyDays,
                                         StatusObligation status,
                                         LocalDate dueDate,
                                         LocalDateTime updatedAt) {
        Obligation obligation = new Obligation(id, customerId, obligationNumber, totalBalance, overdueBalance,
                delinquencyDays, status, dueDate);
        obligation.updatedAt = updatedAt;
        return obligation;
    }

    public void registerDelinquency(int delinquencyDays, BigDecimal overdueBalance) {
        if (delinquencyDays < 0) {
            throw new IllegalArgumentException("delinquencyDays cannot be negative");
        }
        if (overdueBalance == null || overdueBalance.signum() < 0) {
            throw new IllegalArgumentException("overdueBalance cannot be negative");
        }
        this.delinquencyDays = delinquencyDays;
        this.overdueBalance = overdueBalance;
        this.status = delinquencyDays > 0 ? StatusObligation.EN_MORA : StatusObligation.AL_DIA;
        this.updatedAt = LocalDateTime.now();
    }

    public void applyPayment(BigDecimal paymentAmount) {
        if (paymentAmount == null || paymentAmount.signum() <= 0) {
            throw new IllegalArgumentException("paymentAmount must be greater than zero");
        }
        this.totalBalance = this.totalBalance.subtract(paymentAmount).max(BigDecimal.ZERO);
        this.overdueBalance = this.overdueBalance.subtract(paymentAmount).max(BigDecimal.ZERO);
        if (this.totalBalance.signum() == 0) {
            this.status = StatusObligation.CANCELADA;
            this.delinquencyDays = 0;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getObligationNumber() {
        return obligationNumber;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public BigDecimal getOverdueBalance() {
        return overdueBalance;
    }

    public int getDelinquencyDays() {
        return delinquencyDays;
    }

    public StatusObligation getStatus() {
        return status;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public enum StatusObligation {
        AL_DIA,
        EN_MORA,
        REESTRUCTURADA,
        CANCELADA
    }
}
