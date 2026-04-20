package coovitelCobranza.cobranzas.interaction.application.exception;

public class InteractionNotFoundException extends RuntimeException {

    public InteractionNotFoundException(Long id) {
        super("Interaction not found with ID: " + id);
    }
}

