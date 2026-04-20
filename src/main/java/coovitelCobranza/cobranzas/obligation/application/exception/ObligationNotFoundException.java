package coovitelCobranza.cobranzas.obligation.application.exception;

/**
 * Excepción lanzada cuando no se encuentra una obligación en el repositorio.
 * Se utiliza cuando una búsqueda por identificador o número no retorna resultados.
 */
public class ObligationNotFoundException extends RuntimeException {

    /**
     * Construye una excepción cuando no se encuentra una obligación por su identificador.
     *
     * @param id Identificador de la obligación no encontrada
     */
    public ObligationNotFoundException(Long id) {
        super("No se encontro la obligacion con id: " + id);
    }

    /**
     * Construye una excepción cuando no se encuentra una obligación por su número.
     *
     * @param numeroObligation Número de la obligación no encontrada
     */
    public ObligationNotFoundException(String numeroObligation) {
        super("No se encontro la obligacion con numero: " + numeroObligation);
    }
}

