package coovitelCobranza.cobranzas.obligacion.application.exception;

public class ObligacionNotFoundException extends RuntimeException {

    public ObligacionNotFoundException(Long id) {
        super("No se encontro la obligacion con id: " + id);
    }

    public ObligacionNotFoundException(String numeroObligacion) {
        super("No se encontro la obligacion con numero: " + numeroObligacion);
    }
}

