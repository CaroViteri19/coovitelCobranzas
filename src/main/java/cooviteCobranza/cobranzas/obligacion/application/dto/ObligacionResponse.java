package cooviteCobranza.cobranzas.obligacion.application.dto;

import cooviteCobranza.cobranzas.obligacion.domain.model.Obligacion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ObligacionResponse(
        Long id,
        Long clienteId,
        String numeroObligacion,
        BigDecimal saldoTotal,
        BigDecimal saldoVencido,
        int diasMora,
        String estado,
        LocalDate fechaVencimiento,
        LocalDateTime updatedAt
) {

    public static ObligacionResponse fromDomain(Obligacion obligacion) {
        return new ObligacionResponse(
                obligacion.getId(),
                obligacion.getClienteId(),
                obligacion.getNumeroObligacion(),
                obligacion.getSaldoTotal(),
                obligacion.getSaldoVencido(),
                obligacion.getDiasMora(),
                obligacion.getEstado().name(),
                obligacion.getFechaVencimiento(),
                obligacion.getUpdatedAt()
        );
    }
}

