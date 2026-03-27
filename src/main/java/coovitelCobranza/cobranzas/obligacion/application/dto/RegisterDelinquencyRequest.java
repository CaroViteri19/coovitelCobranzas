package coovitelCobranza.cobranzas.obligacion.application.dto;

import java.math.BigDecimal;

public record RegisterDelinquencyRequest(Long obligationId, int delinquencyDays, BigDecimal overdueBalance) {
}

