package coovitelCobranza.cobranzas.scoring.application.dto;

import coovitelCobranza.cobranzas.scoring.domain.model.ScoringSegmentacion;

import java.time.LocalDateTime;

public record ScoringSegmentationResponse(
        Long id,
        Long customerId,
        Long obligationId,
        double score,
        String segment,
        String modelVersion,
        String mainReason,
        LocalDateTime createdAt
) {

    public static ScoringSegmentationResponse fromDomain(ScoringSegmentacion scoring) {
        return new ScoringSegmentationResponse(
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

