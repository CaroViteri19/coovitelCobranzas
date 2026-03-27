package cooviteCobranza.cobranzas.politicas.application.dto;

public record CrearEstrategiaRequest(
        String nombre,
        String descripcion,
        int maxIntentosContacto,
        int diasAntesDeeEscalacion,
        String rolAsignacionEscalada
) {
}

