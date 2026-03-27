package cooviteCobranza.cobranzas.obligacion.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Obligacion {

    private final Long id;
    private final Long clienteId;
    private final String numeroObligacion;
    private BigDecimal saldoTotal;
    private BigDecimal saldoVencido;
    private int diasMora;
    private EstadoObligacion estado;
    private LocalDate fechaVencimiento;
    private LocalDateTime updatedAt;

    private Obligacion(Long id,
                       Long clienteId,
                       String numeroObligacion,
                       BigDecimal saldoTotal,
                       BigDecimal saldoVencido,
                       int diasMora,
                       EstadoObligacion estado,
                       LocalDate fechaVencimiento) {
        this.id = id;
        this.clienteId = Objects.requireNonNull(clienteId, "clienteId es requerido");
        this.numeroObligacion = Objects.requireNonNull(numeroObligacion, "numeroObligacion es requerido");
        this.saldoTotal = Objects.requireNonNull(saldoTotal, "saldoTotal es requerido");
        this.saldoVencido = Objects.requireNonNull(saldoVencido, "saldoVencido es requerido");
        this.diasMora = diasMora;
        this.estado = Objects.requireNonNull(estado, "estado es requerido");
        this.fechaVencimiento = fechaVencimiento;
        this.updatedAt = LocalDateTime.now();
    }

    public static Obligacion crear(Long clienteId, String numeroObligacion, BigDecimal saldoTotal) {
        return new Obligacion(null, clienteId, numeroObligacion, saldoTotal, BigDecimal.ZERO, 0,
                EstadoObligacion.AL_DIA, null);
    }

    public static Obligacion reconstruir(Long id,
                                         Long clienteId,
                                         String numeroObligacion,
                                         BigDecimal saldoTotal,
                                         BigDecimal saldoVencido,
                                         int diasMora,
                                         EstadoObligacion estado,
                                         LocalDate fechaVencimiento,
                                         LocalDateTime updatedAt) {
        Obligacion obligacion = new Obligacion(id, clienteId, numeroObligacion, saldoTotal, saldoVencido,
                diasMora, estado, fechaVencimiento);
        obligacion.updatedAt = updatedAt;
        return obligacion;
    }

    public void registrarMora(int diasMora, BigDecimal saldoVencido) {
        if (diasMora < 0) {
            throw new IllegalArgumentException("diasMora no puede ser negativo");
        }
        if (saldoVencido == null || saldoVencido.signum() < 0) {
            throw new IllegalArgumentException("saldoVencido no puede ser negativo");
        }
        this.diasMora = diasMora;
        this.saldoVencido = saldoVencido;
        this.estado = diasMora > 0 ? EstadoObligacion.EN_MORA : EstadoObligacion.AL_DIA;
        this.updatedAt = LocalDateTime.now();
    }

    public void aplicarPago(BigDecimal valorPago) {
        if (valorPago == null || valorPago.signum() <= 0) {
            throw new IllegalArgumentException("valorPago debe ser mayor a cero");
        }
        this.saldoTotal = this.saldoTotal.subtract(valorPago).max(BigDecimal.ZERO);
        this.saldoVencido = this.saldoVencido.subtract(valorPago).max(BigDecimal.ZERO);
        if (this.saldoTotal.signum() == 0) {
            this.estado = EstadoObligacion.CANCELADA;
            this.diasMora = 0;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public String getNumeroObligacion() {
        return numeroObligacion;
    }

    public BigDecimal getSaldoTotal() {
        return saldoTotal;
    }

    public BigDecimal getSaldoVencido() {
        return saldoVencido;
    }

    public int getDiasMora() {
        return diasMora;
    }

    public EstadoObligacion getEstado() {
        return estado;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public enum EstadoObligacion {
        AL_DIA,
        EN_MORA,
        REESTRUCTURADA,
        CANCELADA
    }
}
