package coovitelCobranza.cobranzas.casogestion.application.exception;

public class CaseNotFoundException extends RuntimeException {

    public CaseNotFoundException(Long id) {
        super("Case not found with ID: " + id);
    }
}

