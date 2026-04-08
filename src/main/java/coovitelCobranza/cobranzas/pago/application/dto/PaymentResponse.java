package coovitelCobranza.cobranzas.pago.application.dto;

import coovitelCobranza.cobranzas.pago.domain.model.Payment;

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

    public static PaymentResponse fromDomain(Payment pago) {
        return new PaymentResponse(
                pago.getId(),
                pago.getObligationId(),
                pago.getAmount(),
                pago.getExternalReference(),
                pago.getMethod().name(),
                pago.getStatus().name(),
                pago.getConfirmedAt(),
                pago.getCreatedAt()
        );
    }
}

