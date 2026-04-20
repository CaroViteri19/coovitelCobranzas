package coovitelCobranza.cobranzas.orchestration.application.exception;

/**
 * Se lanza cuando un use case de orquestación intenta actuar sobre un caso
 * que no existe en el repositorio.
 */
public class CaseForOrchestrationNotFoundException extends RuntimeException {

    public CaseForOrchestrationNotFoundException(Long caseId) {
        super("Caso de gestión no encontrado con ID: " + caseId);
    }
}
