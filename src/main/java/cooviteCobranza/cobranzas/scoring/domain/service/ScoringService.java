package cooviteCobranza.cobranzas.scoring.domain.service;

public interface ScoringService {

    ScoreResult calcularScore(Long clienteId, Long obligacionId);

    record ScoreResult(double score, String segmento, String versionModelo, String razonPrincipal) {
    }
}

