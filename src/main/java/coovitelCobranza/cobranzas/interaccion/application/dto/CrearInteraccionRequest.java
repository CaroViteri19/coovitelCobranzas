package coovitelCobranza.cobranzas.interaccion.application.dto;

public record CrearInteraccionRequest(
        Long casoGestionId,
        String canal,
        String plantilla
) {
}

