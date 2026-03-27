package cooviteCobranza.cobranzas.orquestacion.application.dto;

public record EnviarOrquestacionRequest(
        Long casoGestionId,
        String canal,
        String destino,
        String plantilla
) {
}

