package coovitelCobranza.cobranzas.orchestration.application.dto;

import coovitelCobranza.cobranzas.interaction.domain.model.Interaction;

/**
 * Resultado del intento de envío por un canal específico dentro de una
 * operación multicanal (por ejemplo, envío de link de pago por SMS + Email +
 * WhatsApp a la vez).
 *
 * @param channel       canal usado.
 * @param destination   destino concreto (teléfono o email).
 * @param interactionId id de la {@link Interaction} persistida (puede ser
 *                      {@code null} si no se persistió).
 * @param delivered     {@code true} si el proveedor reportó éxito.
 * @param reason        motivo de skip o fallo (puede ser {@code null} cuando
 *                      todo fue OK).
 */
public record ChannelDispatchResult(
        Interaction.Channel channel,
        String destination,
        Long interactionId,
        boolean delivered,
        String reason
) {

    public static ChannelDispatchResult delivered(Interaction.Channel channel,
                                                  String destination,
                                                  Long interactionId) {
        return new ChannelDispatchResult(channel, destination, interactionId, true, null);
    }

    public static ChannelDispatchResult failed(Interaction.Channel channel,
                                               String destination,
                                               Long interactionId,
                                               String reason) {
        return new ChannelDispatchResult(channel, destination, interactionId, false, reason);
    }

    public static ChannelDispatchResult skipped(Interaction.Channel channel, String reason) {
        return new ChannelDispatchResult(channel, null, null, false, reason);
    }
}
