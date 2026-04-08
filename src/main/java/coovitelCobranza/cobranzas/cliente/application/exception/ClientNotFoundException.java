package coovitelCobranza.cobranzas.cliente.application.exception;

public class ClientNotFoundException extends RuntimeException {

    public ClientNotFoundException(Long id) {
        super("Client no encontrado con ID: " + id);
    }

    public ClientNotFoundException(String tipoDocumento, String numeroDocumento) {
        super("Client no encontrado con documento: " + tipoDocumento + " " + numeroDocumento);
    }
}

