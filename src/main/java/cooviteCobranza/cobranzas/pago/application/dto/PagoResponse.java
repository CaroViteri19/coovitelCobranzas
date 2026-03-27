package cooviteCobranza.cobranzas.pago.application.dto;

import cooviteCobranza.cobranzas.pago.domain.model.Pago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoResponse(
        Long id,
        Long obligacionId,
        BigDecimal valor,
        String referenciaExterna,
        String metodo,
        String estado,
        LocalDateTime confirmadoAt,
        LocalDateTime createdAt
) {

    public static PagoResponse fromDomain(Pago pago) {
        return new PagoResponse(
                pago.getId(),
                pago.getObligacionId(),
                pago.getValor(),
                pago.getReferenciaExterna(),
                pago.getMetodo().name(),
                pago.getEstado().name(),
                pago.getConfirmadoAt(),
                pago.getCreatedAt()
        );
    }
}

