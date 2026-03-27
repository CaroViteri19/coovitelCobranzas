package cooviteCobranza.cobranzas.politicas.application.exception;

public class EstrategiaNotFoundException extends RuntimeException {
    public EstrategiaNotFoundException(Long id) {
        super("Estrategia no encontrada con ID: " + id);
    }

    public EstrategiaNotFoundException(String nombre) {
        super("Estrategia no encontrada con nombre: " + nombre);
    }
}

