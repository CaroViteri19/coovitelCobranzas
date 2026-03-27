package coovitelCobranza.cobranzas.scoring.application.exception;

public class ScoringSegmentationBusinessException extends RuntimeException {

    public ScoringSegmentationBusinessException(String message) {
        super(message);
    }

    public ScoringSegmentationBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

