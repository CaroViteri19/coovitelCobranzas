package coovitelCobranza.cobranzas.interaccion.application.dto;

/**
 * DTO para solicitar la creación de una nueva interacción.
 *
 * Esta clase encapsula los datos necesarios para registrar una nueva comunicación
 * con un cliente en un caso de cobranza específico.
 */
public record CreateInteractionRequest(
        /**
         * ID del caso de cobranza asociado a esta interacción.
         */
        Long caseId,

        /**
         * Canal de comunicación a utilizar (SMS, WHATSAPP, EMAIL, VOICE).
         */
        String channel,

        /**
         * Plantilla de mensaje a usar en la interacción.
         */
        String template
) {
}

