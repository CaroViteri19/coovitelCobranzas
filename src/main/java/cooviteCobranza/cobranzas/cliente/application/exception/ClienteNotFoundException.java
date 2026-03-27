package cooviteCobranza.cobranzas.cliente.application.exception;

public class ClienteNotFoundException extends RuntimeException {

    public ClienteNotFoundException(Long id) {
        super("Cliente no encontrado con ID: " + id);
    }

    public ClienteNotFoundException(String tipoDocumento, String numeroDocumento) {
        super("Cliente no encontrado con documento: " + tipoDocumento + " " + numeroDocumento);
    }
}

