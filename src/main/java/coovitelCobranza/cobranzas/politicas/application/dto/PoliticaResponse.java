package coovitelCobranza.cobranzas.politicas.application.dto;

import coovitelCobranza.cobranzas.politicas.domain.model.Politica;

import java.time.LocalDateTime;

public record PoliticaResponse(
        Long id,
        Long estrategiaId,
        String tipoCobro,
        int diasMoraMinimo,
        int diasMoraMaximo,
        String accion,
        boolean activa,
        LocalDateTime updatedAt
) {

    public static PoliticaResponse fromDomain(Politica politica) {
        return new PoliticaResponse(
                politica.getId(),
                politica.getEstrategiaId(),
                politica.getTipoCobro().name(),
                politica.getDiasMoraMinimo(),
                politica.getDiasMoraMaximo(),
                politica.getAccion(),
                politica.isActiva(),
                politica.getUpdatedAt()
        );
    }
}

