package cooviteCobranza.cobranzas.casogestion.application.exception;

public class CasoGestionNotFoundException extends RuntimeException {

    public CasoGestionNotFoundException(Long id) {
        super("Caso de gestión no encontrado con ID: " + id);
    }
}

