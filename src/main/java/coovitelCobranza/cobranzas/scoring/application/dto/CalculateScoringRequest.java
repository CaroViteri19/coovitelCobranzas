package coovitelCobranza.cobranzas.scoring.application.dto;

import java.math.BigDecimal;

public record CalculateScoringRequest(
        Long customerId,
        Long obligationId,
        int delinquencyDays,
        BigDecimal overdueBalance,
        int contactAttempts
) {
}

