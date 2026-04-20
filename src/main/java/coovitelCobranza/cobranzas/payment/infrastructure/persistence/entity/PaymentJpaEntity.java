package coovitelCobranza.cobranzas.payment.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class PaymentJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "obligation_id", nullable = false)
    private Long obligationId;

    @Column(name = "amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Column(name = "external_reference", nullable = false, unique = true, length = 50)
    private String externalReference;

    @Column(name = "method", nullable = false, length = 20)
    private String method;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Constructores
    public PaymentJpaEntity() {
    }

    public PaymentJpaEntity(Long id, Long obligationId, BigDecimal amount, String externalReference,
                        String method, String status, LocalDateTime confirmedAt, LocalDateTime createdAt) {
        this.id = id;
        this.obligationId = obligationId;
        this.amount = amount;
        this.externalReference = externalReference;
        this.method = method;
        this.status = status;
        this.confirmedAt = confirmedAt;
        this.createdAt = createdAt;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getObligationId() {
        return obligationId;
    }

    public void setObligationId(Long obligationId) {
        this.obligationId = obligationId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

