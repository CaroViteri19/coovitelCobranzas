package cooviteCobranza.cobranzas.interaccion.application.exception;

public class InteraccionNotFoundException extends RuntimeException {

    public InteraccionNotFoundException(Long id) {
        super("Interacción no encontrada con ID: " + id);
    }
}

