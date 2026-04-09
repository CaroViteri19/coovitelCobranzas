package coovitelCobranza.cobranzas.obligacion.application.dto;

import coovitelCobranza.cobranzas.obligacion.domain.model.Obligation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de respuesta que contiene la información completa de una obligación.
 *
 * @param id Identificador único de la obligación
 * @param customerId Identificador del cliente propietario de la obligación
 * @param obligationNumber Número de la obligación
 * @param totalBalance Saldo total pendiente de la obligación
 * @param overdueBalance Saldo vencido de la obligación
 * @param delinquencyDays Número de días de mora
 * @param status Estado de la obligación (AL_DIA, EN_MORA, REESTRUCTURADA, CANCELADA)
 * @param dueDate Fecha de vencimiento de la obligación
 * @param updatedAt Fecha y hora de la última actualización
 */
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

    /**
     * Convierte un modelo de dominio Obligation a su representación en DTO.
     *
     * @param obligacion La obligación del dominio a convertir
     * @return DTO con la información de la obligación
     */
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

