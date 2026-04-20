package coovitelCobranza.cobranzas.orchestration.domain.gateway;

/**
 * Resultado técnico del intento de entrega realizado por un {@code Sender}.
 *
 * <p>El servicio de orquestación traduce este resultado a
 * {@code Interaction.ResultStatus} antes de persistir la interacción.</p>
 *
 * @param success             {@code true} si el canal aceptó el mensaje para entrega.
 * @param providerMessageId   id devuelto por el proveedor (null en stubs).
 * @param errorDescription    descripción del error cuando {@code success=false}.
 */
public record DeliveryResult(
        boolean success,
        String providerMessageId,
        String errorDescription
) {
    public static DeliveryResult ok(String providerMessageId) {
        return new DeliveryResult(true, providerMessageId, null);
    }

    public static DeliveryResult failed(String errorDescription) {
        return new DeliveryResult(false, null, errorDescription);
    }
}
