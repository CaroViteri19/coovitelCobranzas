package coovitelCobranza.cobranzas.orchestration.application.exception;

public class OrchestrationNotFoundException extends RuntimeException {

    public OrchestrationNotFoundException(Long id) {
        super("Ejecución de orquestación no encontrada con ID: " + id);
    }
}

