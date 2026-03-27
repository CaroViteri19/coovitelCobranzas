package cooviteCobranza.cobranzas.politicas.application.exception;

public class PoliticaNotFoundException extends RuntimeException {
    public PoliticaNotFoundException(Long id) {
        super("Política no encontrada con ID: " + id);
    }
}

