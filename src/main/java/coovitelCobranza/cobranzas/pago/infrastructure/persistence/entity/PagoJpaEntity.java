package coovitelCobranza.cobranzas.pago.infrastructure.persistence;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
public class PagoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long obligacionId;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false, unique = true, length = 50)
    private String referenciaExterna;

    @Column(nullable = false, length = 20)
    private String metodo;

    @Column(nullable = false, length = 20)
    private String estado;

    @Column
    private LocalDateTime confirmadoAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Constructores
    public PagoJpaEntity() {
    }

    public PagoJpaEntity(Long id, Long obligacionId, BigDecimal valor, String referenciaExterna,
                        String metodo, String estado, LocalDateTime confirmadoAt, LocalDateTime createdAt) {
        this.id = id;
        this.obligacionId = obligacionId;
        this.valor = valor;
        this.referenciaExterna = referenciaExterna;
        this.metodo = metodo;
        this.estado = estado;
        this.confirmadoAt = confirmadoAt;
        this.createdAt = createdAt;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getObligacionId() {
        return obligacionId;
    }

    public void setObligacionId(Long obligacionId) {
        this.obligacionId = obligacionId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getReferenciaExterna() {
        return referenciaExterna;
    }

    public void setReferenciaExterna(String referenciaExterna) {
        this.referenciaExterna = referenciaExterna;
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getConfirmadoAt() {
        return confirmadoAt;
    }

    public void setConfirmadoAt(LocalDateTime confirmadoAt) {
        this.confirmadoAt = confirmadoAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

