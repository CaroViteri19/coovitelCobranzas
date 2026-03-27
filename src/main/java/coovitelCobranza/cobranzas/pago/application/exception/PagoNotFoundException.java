package coovitelCobranza.cobranzas.pago.application.exception;

public class PagoNotFoundException extends RuntimeException {

    public PagoNotFoundException(Long id) {
        super("Pago no encontrado con ID: " + id);
    }

    public PagoNotFoundException(String referenciaExterna) {
        super("Pago no encontrado con referencia: " + referenciaExterna);
    }
}

