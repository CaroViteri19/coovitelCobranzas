package coovitelCobranza.cobranzas.policies.application.exception;

public class PolicyNotFoundException extends RuntimeException {
    public PolicyNotFoundException(Long id) {
        super("Policy not found with ID: " + id);
    }
}

