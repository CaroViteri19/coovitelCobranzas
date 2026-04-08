package coovitelCobranza.cobranzas.scoring.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Unit Tests - ScoringRulesService")
class ScoringRulesServiceTest {

    private final ScoringRulesService service = new ScoringRulesService();

    @Test
    @DisplayName("Score low when there is no risk")
    void scoreLowWithoutRisk() {
        ScoringService.ScoreResult result = service.calculateWithData(0, BigDecimal.ZERO, 0);

        assertEquals("LOW", result.segment());
        assertEquals(ScoringRulesService.MODEL_VERSION, result.modelVersion());
        assertTrue(result.score() >= 0.0 && result.score() < 35.0);
    }

    @Test
    @DisplayName("Score high with high delinquency, balance and attempts")
    void scoreHighWithTotalRisk() {
        ScoringService.ScoreResult result = service.calculateWithData(180, new BigDecimal("10000000"), 10);

        assertEquals(100.0, result.score());
        assertEquals("HIGH", result.segment());
    }

    @Test
    @DisplayName("Medium segment in the middle range")
    void mediumSegment() {
        ScoringService.ScoreResult result = service.calculateWithData(120, new BigDecimal("2000000"), 4);

        assertEquals("MEDIUM", result.segment());
        assertTrue(result.score() >= 35.0 && result.score() < 65.0);
    }

    @Test
    @DisplayName("Main reason favors delinquency when it dominates")
    void mainReasonDelinquency() {
        ScoringService.ScoreResult result = service.calculateWithData(180, new BigDecimal("0"), 0);

        assertEquals("High impact due to delinquency days", result.mainReason());
    }

    @Test
    @DisplayName("Negative values are normalized")
    void normalizesNegativeValues() {
        ScoringService.ScoreResult result = service.calculateWithData(-10, new BigDecimal("-5"), -2);

        assertEquals("LOW", result.segment());
        assertEquals(0.0, result.score());
    }
}


