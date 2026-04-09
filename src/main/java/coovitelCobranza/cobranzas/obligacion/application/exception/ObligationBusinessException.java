package coovitelCobranza.cobranzas.obligacion.application.exception;

/**
 * Excepción lanzada cuando ocurre un error de lógica de negocio en operaciones sobre obligaciones.
 * Esta excepción se utiliza para indicar condiciones de negocio que no se pueden completar,
 * como intentar aplicar un pago inválido o registrar morosidad con datos inconsistentes.
 */
public class ObligationBusinessException extends RuntimeException {

    /**
     * Construye una excepción con el mensaje de error especificado.
     *
     * @param message Descripción del error de negocio
     */
    public ObligationBusinessException(String message) {
        super(message);
    }
}

