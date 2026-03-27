package cooviteCobranza.cobranzas.scoring.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Unit Tests - ReglasScoringService")
class ReglasScoringServiceTest {

    private final ReglasScoringService service = new ReglasScoringService();

    @Test
    @DisplayName("Score bajo cuando no hay riesgo")
    void scoreBajoSinRiesgo() {
        ScoringService.ScoreResult result = service.calcularConDatos(0, BigDecimal.ZERO, 0);

        assertEquals("BAJO", result.segmento());
        assertEquals(ReglasScoringService.VERSION_MODELO, result.versionModelo());
        assertTrue(result.score() >= 0.0 && result.score() < 35.0);
    }

    @Test
    @DisplayName("Score alto con alta mora, saldo e intentos")
    void scoreAltoConRiesgoTotal() {
        ScoringService.ScoreResult result = service.calcularConDatos(180, new BigDecimal("10000000"), 10);

        assertEquals(100.0, result.score());
        assertEquals("ALTO", result.segmento());
    }

    @Test
    @DisplayName("Segmento MEDIO en rango intermedio")
    void segmentoMedio() {
        ScoringService.ScoreResult result = service.calcularConDatos(120, new BigDecimal("2000000"), 4);

        assertEquals("MEDIO", result.segmento());
        assertTrue(result.score() >= 35.0 && result.score() < 65.0);
    }

    @Test
    @DisplayName("Razón principal mora cuando es factor dominante")
    void razonPrincipalMora() {
        ScoringService.ScoreResult result = service.calcularConDatos(180, new BigDecimal("0"), 0);

        assertEquals("Alto impacto por dias de mora", result.razonPrincipal());
    }

    @Test
    @DisplayName("Valores negativos se normalizan")
    void normalizaValoresNegativos() {
        ScoringService.ScoreResult result = service.calcularConDatos(-10, new BigDecimal("-5"), -2);

        assertEquals("BAJO", result.segmento());
        assertEquals(0.0, result.score());
    }
}


