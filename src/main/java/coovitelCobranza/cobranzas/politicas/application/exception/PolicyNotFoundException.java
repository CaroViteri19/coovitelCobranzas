package coovitelCobranza.cobranzas.politicas.application.exception;

public class PolicyNotFoundException extends RuntimeException {
    public PolicyNotFoundException(Long id) {
        super("Policy not found with ID: " + id);
    }
}

