package coovitelCobranza.cobranzas.scoring.application.dto;

import coovitelCobranza.cobranzas.scoring.domain.model.ScoringSegmentation;

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

    public static ScoringSegmentationResponse fromDomain(ScoringSegmentation scoring) {
        return new ScoringSegmentationResponse(
                scoring.getId(),
                scoring.getCustomerId(),
                scoring.getObligationId(),
                scoring.getScore(),
                scoring.getSegment(),
                scoring.getModelVersion(),
                scoring.getMainReason(),
                scoring.getCreatedAt()
        );
    }
}

