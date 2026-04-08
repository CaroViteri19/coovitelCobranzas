package coovitelCobranza.cobranzas.obligacion.application.exception;

public class ObligationNotFoundException extends RuntimeException {

    public ObligationNotFoundException(Long id) {
        super("No se encontro la obligacion con id: " + id);
    }

    public ObligationNotFoundException(String numeroObligation) {
        super("No se encontro la obligacion con numero: " + numeroObligation);
    }
}

