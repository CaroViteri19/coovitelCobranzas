package coovitelCobranza.cobranzas.orquestacion.application.exception;

public class OrquestacionNotFoundException extends RuntimeException {

    public OrquestacionNotFoundException(Long id) {
        super("Ejecución de orquestación no encontrada con ID: " + id);
    }
}

