package coovitelCobranza.cobranzas.obligacion.application.dto;

import coovitelCobranza.cobranzas.obligacion.domain.model.Obligation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ObligationResponse(
        Long id,
        Long customerId,
        String obligationNumber,
        BigDecimal totalBalance,
        BigDecimal overdueBalance,
        int delinquencyDays,
        String status,
        LocalDate dueDate,
        LocalDateTime updatedAt
) {

    public static ObligationResponse fromDomain(Obligation obligacion) {
        return new ObligationResponse(
                obligacion.getId(),
                obligacion.getCustomerId(),
                obligacion.getObligationNumber(),
                obligacion.getTotalBalance(),
                obligacion.getOverdueBalance(),
                obligacion.getDelinquencyDays(),
                obligacion.getStatus().name(),
                obligacion.getDueDate(),
                obligacion.getUpdatedAt()
        );
    }
}

