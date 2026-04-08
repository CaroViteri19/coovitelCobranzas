package coovitelCobranza.cobranzas.interaccion.application.exception;

public class InteractionNotFoundException extends RuntimeException {

    public InteractionNotFoundException(Long id) {
        super("Interaction not found with ID: " + id);
    }
}

