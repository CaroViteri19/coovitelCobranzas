package coovitelCobranza.cobranzas.orchestration.application.exception;

/**
 * Se lanza cuando un use case de orquestación no puede localizar al cliente
 * dueño de la obligación asociada al caso.
 */
public class ClientForOrchestrationNotFoundException extends RuntimeException {

    public ClientForOrchestrationNotFoundException(Long customerId) {
        super("Cliente no encontrado con ID: " + customerId);
    }
}
