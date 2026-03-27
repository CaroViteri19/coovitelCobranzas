package coovitelCobranza.cobranzas.obligacion.application.dto;

import java.math.BigDecimal;

public record ApplyObligationPaymentRequest(Long obligationId, BigDecimal paymentAmount) {
}

