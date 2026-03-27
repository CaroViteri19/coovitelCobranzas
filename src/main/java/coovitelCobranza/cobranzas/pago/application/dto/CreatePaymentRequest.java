package coovitelCobranza.cobranzas.pago.application.dto;

import java.math.BigDecimal;

public record CreatePaymentRequest(
        Long obligationId,
        BigDecimal amount,
        String externalReference,
        String method
) {
}

