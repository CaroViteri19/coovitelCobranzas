package coovitelCobranza.cobranzas.scoring.application.exception;

public class ScoringSegmentationNotFoundException extends RuntimeException {

    public ScoringSegmentationNotFoundException(Long id) {
        super("Scoring not found with ID: " + id);
    }

    public ScoringSegmentationNotFoundException(String reference) {
        super("Scoring not found for: " + reference);
    }
}

