package coovitelCobranza.cobranzas.casogestion.application.dto;

/**
 * DTO para asignar un asesor a un caso.
 *
 * @param advisor el nombre o identificador del asesor a asignar
 */
public record AssignAdvisorRequest(
        String advisor
) {
}

