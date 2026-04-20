package coovitelCobranza.cobranzas.interaction.application.dto;

/**
 * DTO para solicitar la obtención de una interacción específica por su ID.
 *
 * Esta clase encapsula el identificador único de la interacción que se desea recuperar.
 */
public record GetInteractionByIdRequest(
        /**
         * ID único de la interacción a recuperar.
         */
        Long interactionId
) {
}

