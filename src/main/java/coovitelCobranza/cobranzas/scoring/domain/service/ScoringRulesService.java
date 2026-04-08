package coovitelCobranza.cobranzas.scoring.domain.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ScoringRulesService implements ScoringService {

    public static final String MODEL_VERSION = "rules-v1";

    @Override
    public ScoreResult calculateScore(Long customerId, Long obligationId) {
        // Dummy implementation kept for current contract compatibility.
        return new ScoreResult(0.0, "LOW", MODEL_VERSION, "No input data available");
    }

    public ScoreResult calculateWithData(int delinquencyDays, BigDecimal overdueBalance, int contactAttempts) {
        double delinquencyScore = Math.min(Math.max(delinquencyDays, 0), 180) / 180.0 * 50.0;
        double balance = overdueBalance != null ? Math.max(overdueBalance.doubleValue(), 0.0) : 0.0;
        double balanceScore = Math.min(balance / 10_000_000.0, 1.0) * 30.0;
        double attemptsScore = Math.min(Math.max(contactAttempts, 0), 10) / 10.0 * 20.0;

        double totalScore = Math.min(delinquencyScore + balanceScore + attemptsScore, 100.0);
        String segment = segmentByScore(totalScore);
        String mainReason = buildMainReason(delinquencyScore, balanceScore, attemptsScore);

        return new ScoreResult(totalScore, segment, MODEL_VERSION, mainReason);
    }

    private String segmentByScore(double score) {
        if (score >= 65.0) {
            return "HIGH";
        }
        if (score >= 35.0) {
            return "MEDIUM";
        }
        return "LOW";
    }

    private String buildMainReason(double delinquencyScore, double balanceScore, double attemptsScore) {
        if (delinquencyScore >= balanceScore && delinquencyScore >= attemptsScore) {
            return "High impact due to delinquency days";
        }
        if (balanceScore >= attemptsScore) {
            return "High impact due to overdue balance";
        }
        return "High impact due to contact attempts";
    }
}
