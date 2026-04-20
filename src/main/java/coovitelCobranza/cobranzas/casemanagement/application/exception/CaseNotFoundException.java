package coovitelCobranza.cobranzas.casemanagement.application.exception;

/**
 * Excepción que se lanza cuando se intenta acceder a un caso que no existe
 * en la base de datos.
 */
public class CaseNotFoundException extends RuntimeException {

    /**
     * Construye una excepción indicando que el caso con el ID especificado no fue encontrado.
     *
     * @param id el ID del caso que no pudo ser encontrado
     */
    public CaseNotFoundException(Long id) {
        super("Case not found with ID: " + id);
    }
}

