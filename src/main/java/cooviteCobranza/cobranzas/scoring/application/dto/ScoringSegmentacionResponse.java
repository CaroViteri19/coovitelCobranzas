package cooviteCobranza.cobranzas.scoring.application.dto;

import cooviteCobranza.cobranzas.scoring.domain.model.ScoringSegmentacion;

import java.time.LocalDateTime;

public record ScoringSegmentacionResponse(
        Long id,
        Long clienteId,
        Long obligacionId,
        double score,
        String segmento,
        String versionModelo,
        String razonPrincipal,
        LocalDateTime createdAt
) {

    public static ScoringSegmentacionResponse fromDomain(ScoringSegmentacion scoring) {
        return new ScoringSegmentacionResponse(
                scoring.getId(),
                scoring.getClienteId(),
                scoring.getObligacionId(),
                scoring.getScore(),
                scoring.getSegmento(),
                scoring.getVersionModelo(),
                scoring.getRazonPrincipal(),
                scoring.getCreatedAt()
        );
    }
}

