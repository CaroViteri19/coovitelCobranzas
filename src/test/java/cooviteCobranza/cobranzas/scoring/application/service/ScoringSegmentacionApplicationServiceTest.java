package cooviteCobranza.cobranzas.scoring.application.service;

import cooviteCobranza.cobranzas.scoring.application.dto.CalcularScoringRequest;
import cooviteCobranza.cobranzas.scoring.application.dto.ScoringSegmentacionResponse;
import cooviteCobranza.cobranzas.scoring.application.exception.ScoringSegmentacionBusinessException;
import cooviteCobranza.cobranzas.scoring.application.exception.ScoringSegmentacionNotFoundException;
import cooviteCobranza.cobranzas.scoring.domain.model.ScoringSegmentacion;
import cooviteCobranza.cobranzas.scoring.domain.repository.ScoringSegmentacionRepository;
import cooviteCobranza.cobranzas.scoring.domain.service.ReglasScoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests - ScoringSegmentacionApplicationService")
class ScoringSegmentacionApplicationServiceTest {

    @Mock
    private ScoringSegmentacionRepository repository;

    private ScoringSegmentacionApplicationService service;

    @BeforeEach
    void setUp() {
        service = new ScoringSegmentacionApplicationService(repository, new ReglasScoringService());
    }

    @Test
    @DisplayName("Calcular y guardar scoring")
    void calcularYGuardar() {
        CalcularScoringRequest request = new CalcularScoringRequest(1L, 10L, 30, new BigDecimal("500000"), 2);
        ScoringSegmentacion saved = ScoringSegmentacion.reconstruir(
                100L, 1L, 10L, 42.0, "MEDIO", "rules-v1", "Alto impacto por dias de mora", LocalDateTime.now()
        );

        when(repository.save(any(ScoringSegmentacion.class))).thenReturn(saved);

        ScoringSegmentacionResponse response = service.calcular(request);

        assertEquals(100L, response.id());
        assertEquals(1L, response.clienteId());
        assertEquals(10L, response.obligacionId());
    }

    @Test
    @DisplayName("Falla si clienteId u obligacionId son nulos")
    void validarCamposRequeridos() {
        CalcularScoringRequest request = new CalcularScoringRequest(null, 10L, 0, BigDecimal.ZERO, 0);

        assertThrows(ScoringSegmentacionBusinessException.class, () -> service.calcular(request));
    }

    @Test
    @DisplayName("Obtener por id lanza not found cuando no existe")
    void obtenerPorIdNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ScoringSegmentacionNotFoundException.class, () -> service.obtenerPorId(1L));
    }

    @Test
    @DisplayName("Listar por cliente devuelve historial")
    void listarPorCliente() {
        ScoringSegmentacion a = ScoringSegmentacion.reconstruir(
                1L, 5L, 10L, 20.0, "BAJO", "rules-v1", "x", LocalDateTime.now()
        );
        ScoringSegmentacion b = ScoringSegmentacion.reconstruir(
                2L, 5L, 11L, 70.0, "ALTO", "rules-v1", "y", LocalDateTime.now()
        );

        when(repository.findByClienteIdOrderByCreatedAtDesc(5L)).thenReturn(List.of(a, b));

        List<ScoringSegmentacionResponse> result = service.listarPorCliente(5L);
        assertEquals(2, result.size());
    }
}

