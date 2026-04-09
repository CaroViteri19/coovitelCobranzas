package coovitelCobranza.cobranzas.cliente.application.exception;

/**
 * Excepción lanzada cuando no se encuentra un cliente en el sistema.
 * Puede ser causada por búsqueda por ID o por datos de documento.
 */
public class ClientNotFoundException extends RuntimeException {

    /**
     * Construye una excepción cuando no se encuentra cliente por ID.
     *
     * @param id identificador único del cliente no encontrado
     */
    public ClientNotFoundException(Long id) {
        super("Client no encontrado con ID: " + id);
    }

    /**
     * Construye una excepción cuando no se encuentra cliente por documento.
     *
     * @param tipoDocumento tipo de documento del cliente
     * @param numeroDocumento número de documento del cliente
     */
    public ClientNotFoundException(String tipoDocumento, String numeroDocumento) {
        super("Client no encontrado con documento: " + tipoDocumento + " " + numeroDocumento);
    }
}

