package cooviteCobranza.cobranzas.pago.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Pago {

    private final Long id;
    private final Long obligacionId;
    private final BigDecimal valor;
    private final String referenciaExterna;
    private final MetodoPago metodo;
    private EstadoPago estado;
    private LocalDateTime confirmadoAt;
    private final LocalDateTime createdAt;

    private Pago(Long id,
                 Long obligacionId,
                 BigDecimal valor,
                 String referenciaExterna,
                 MetodoPago metodo,
                 EstadoPago estado,
                 LocalDateTime confirmadoAt,
                 LocalDateTime createdAt) {
        this.id = id;
        this.obligacionId = Objects.requireNonNull(obligacionId, "obligacionId es requerido");
        this.valor = Objects.requireNonNull(valor, "valor es requerido");
        this.referenciaExterna = Objects.requireNonNull(referenciaExterna, "referenciaExterna es requerida");
        this.metodo = Objects.requireNonNull(metodo, "metodo es requerido");
        this.estado = Objects.requireNonNull(estado, "estado es requerido");
        this.confirmadoAt = confirmadoAt;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public static Pago crearPendiente(Long obligacionId, BigDecimal valor, String referenciaExterna, MetodoPago metodo) {
        return new Pago(null, obligacionId, valor, referenciaExterna, metodo, EstadoPago.PENDIENTE, null,
                LocalDateTime.now());
    }

    public static Pago reconstruir(Long id,
                                   Long obligacionId,
                                   BigDecimal valor,
                                   String referenciaExterna,
                                   MetodoPago metodo,
                                   EstadoPago estado,
                                   LocalDateTime confirmadoAt,
                                   LocalDateTime createdAt) {
        return new Pago(id, obligacionId, valor, referenciaExterna, metodo, estado, confirmadoAt, createdAt);
    }

    public void confirmar() {
        this.estado = EstadoPago.CONFIRMADO;
        this.confirmadoAt = LocalDateTime.now();
    }

    public void rechazar() {
        this.estado = EstadoPago.RECHAZADO;
    }

    public Long getId() {
        return id;
    }

    public Long getObligacionId() {
        return obligacionId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public String getReferenciaExterna() {
        return referenciaExterna;
    }

    public MetodoPago getMetodo() {
        return metodo;
    }

    public EstadoPago getEstado() {
        return estado;
    }

    public LocalDateTime getConfirmadoAt() {
        return confirmadoAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public enum MetodoPago {
        PSE,
        TARJETA,
        TRANSFERENCIA,
        OFICINA
    }

    public enum EstadoPago {
        PENDIENTE,
        CONFIRMADO,
        RECHAZADO,
        EXPIRADO
    }
}

