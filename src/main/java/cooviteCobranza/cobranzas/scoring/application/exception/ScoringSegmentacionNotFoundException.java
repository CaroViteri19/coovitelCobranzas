package cooviteCobranza.cobranzas.scoring.application.exception;

public class ScoringSegmentacionNotFoundException extends RuntimeException {

    public ScoringSegmentacionNotFoundException(Long id) {
        super("Scoring no encontrado con ID: " + id);
    }

    public ScoringSegmentacionNotFoundException(String referencia) {
        super("Scoring no encontrado para: " + referencia);
    }
}

