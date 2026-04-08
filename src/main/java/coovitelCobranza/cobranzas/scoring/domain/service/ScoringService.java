package coovitelCobranza.cobranzas.scoring.domain.service;

public interface ScoringService {

    ScoreResult calculateScore(Long customerId, Long obligationId);

    record ScoreResult(double score, String segment, String modelVersion, String mainReason) {
    }
}
