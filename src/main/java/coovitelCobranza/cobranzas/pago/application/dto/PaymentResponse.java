package coovitelCobranza.cobranzas.pago.application.dto;

import coovitelCobranza.cobranzas.pago.domain.model.Pago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        Long obligationId,
        BigDecimal amount,
        String externalReference,
        String method,
        String status,
        LocalDateTime confirmedAt,
        LocalDateTime createdAt
) {

    public static PaymentResponse fromDomain(Pago pago) {
        return new PaymentResponse(
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

