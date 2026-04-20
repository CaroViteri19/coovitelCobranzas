package coovitelCobranza.cobranzas.obligation.application.dto;

import java.math.BigDecimal;

/**
 * DTO para registrar o actualizar la morosidad de una obligación.
 *
 * @param obligationId Identificador único de la obligación
 * @param delinquencyDays Número de días de mora
 * @param overdueBalance Saldo vencido de la obligación
 */
public record RegisterDelinquencyRequest(Long obligationId, int delinquencyDays, BigDecimal overdueBalance) {
}

