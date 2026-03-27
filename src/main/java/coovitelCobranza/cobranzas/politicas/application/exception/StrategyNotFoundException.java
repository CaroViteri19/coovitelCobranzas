package coovitelCobranza.cobranzas.politicas.application.exception;

public class StrategyNotFoundException extends RuntimeException {
    public StrategyNotFoundException(Long id) {
        super("Strategy not found with ID: " + id);
    }

    public StrategyNotFoundException(String name) {
        super("Strategy not found with name: " + name);
    }
}

