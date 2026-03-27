package coovitelCobranza.cobranzas.scoring.domain.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ReglasScoringService implements ScoringService {

    public static final String VERSION_MODELO = "rules-v1";

    @Override
    public ScoreResult calcularScore(Long clienteId, Long obligacionId) {
        // Implementación dummy para compatibilidad con el contrato actual.
        return new ScoreResult(0.0, "BAJO", VERSION_MODELO, "Sin datos de entrada");
    }

    public ScoreResult calcularConDatos(int diasMora, BigDecimal saldoVencido, int intentosContacto) {
        double scoreMora = Math.min(Math.max(diasMora, 0), 180) / 180.0 * 50.0;
        double saldo = saldoVencido != null ? Math.max(saldoVencido.doubleValue(), 0.0) : 0.0;
        double scoreSaldo = Math.min(saldo / 10_000_000.0, 1.0) * 30.0;
        double scoreIntentos = Math.min(Math.max(intentosContacto, 0), 10) / 10.0 * 20.0;

        double scoreTotal = Math.min(scoreMora + scoreSaldo + scoreIntentos, 100.0);
        String segmento = segmentar(scoreTotal);
        String razonPrincipal = razonPrincipal(scoreMora, scoreSaldo, scoreIntentos);

        return new ScoreResult(scoreTotal, segmento, VERSION_MODELO, razonPrincipal);
    }

    private String segmentar(double score) {
        if (score >= 65.0) {
            return "ALTO";
        }
        if (score >= 35.0) {
            return "MEDIO";
        }
        return "BAJO";
    }

    private String razonPrincipal(double scoreMora, double scoreSaldo, double scoreIntentos) {
        if (scoreMora >= scoreSaldo && scoreMora >= scoreIntentos) {
            return "Alto impacto por dias de mora";
        }
        if (scoreSaldo >= scoreIntentos) {
            return "Alto impacto por saldo vencido";
        }
        return "Alto impacto por intentos de contacto";
    }
}

