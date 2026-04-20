package coovitelCobranza.cobranzas.payment.application.dto;

import coovitelCobranza.cobranzas.payment.domain.model.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO que representa la respuesta de un pago al cliente.
 * Contiene toda la información relevante de un pago en el sistema.
 */
public record PaymentResponse(
        /**
         * Identificador único del pago.
         */
        Long id,
        /**
         * Identificador de la obligación asociada.
         */
        Long obligationId,
        /**
         * Monto del pago.
         */
        BigDecimal amount,
        /**
         * Referencia externa del pago.
         */
        String externalReference,
        /**
         * Método de pago utilizado.
         */
        String method,
        /**
         * Estado actual del pago (PENDING, CONFIRMED, REJECTED, EXPIRED).
         */
        String status,
        /**
         * Fecha y hora en que se confirmó el pago.
         */
        LocalDateTime confirmedAt,
        /**
         * Fecha y hora de creación del pago.
         */
        LocalDateTime createdAt
) {

    /**
     * Convierte un objeto de dominio Payment a PaymentResponse.
     *
     * @param pago objeto de dominio a convertir
     * @return DTO con la información del pago
     */
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

