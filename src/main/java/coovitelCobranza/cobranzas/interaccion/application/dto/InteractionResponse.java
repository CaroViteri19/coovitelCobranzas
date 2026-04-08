package coovitelCobranza.cobranzas.interaccion.application.dto;

import coovitelCobranza.cobranzas.interaccion.domain.model.Interaction;

import java.time.LocalDateTime;

/**
 * DTO que representa los datos de una interacción en la respuesta HTTP.
 *
 * Esta clase encapsula la información de una interacción lista para ser enviada al cliente.
 */
public record InteractionResponse(
        /**
         * ID único de la interacción.
         */
        Long id,

        /**
         * ID del caso de cobranza asociado.
         */
        Long caseId,

        /**
         * Canal de comunicación utilizado (SMS, WHATSAPP, EMAIL, VOICE).
         */
        String channel,

        /**
         * Plantilla de mensaje utilizada.
         */
        String template,

        /**
         * Estado del resultado de la interacción (PENDING, DELIVERED, READ, etc.).
         */
        String result,

        /**
         * Fecha y hora de creación de la interacción.
         */
        LocalDateTime createdAt
) {

    /**
     * Convierte una entidad de dominio Interaction a su representación en DTO.
     *
     * @param interaction la interacción del dominio a convertir
     * @return DTO con los datos de la interacción
     */
    public static InteractionResponse fromDomain(Interaction interaction) {
        return new InteractionResponse(
                interaction.getId(),
                interaction.getCaseId(),
                interaction.getChannel().name(),
                interaction.getTemplate(),
                interaction.getResultStatus().name(),
                interaction.getCreatedAt()
        );
    }
}

