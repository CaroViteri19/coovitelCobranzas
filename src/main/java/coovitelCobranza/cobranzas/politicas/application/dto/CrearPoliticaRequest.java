package coovitelCobranza.cobranzas.politicas.application.dto;

public record CrearPoliticaRequest(
        Long estrategiaId,
        String tipoCobro,
        int diasMoraMinimo,
        int diasMoraMaximo,
        String accion
) {
}

