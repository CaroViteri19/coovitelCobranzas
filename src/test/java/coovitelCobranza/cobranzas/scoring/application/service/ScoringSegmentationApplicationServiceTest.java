package coovitelCobranza.cobranzas.scoring.application.service;

import coovitelCobranza.cobranzas.scoring.application.dto.CalculateScoringRequest;
import coovitelCobranza.cobranzas.scoring.application.dto.ScoringSegmentationResponse;
import coovitelCobranza.cobranzas.scoring.application.exception.ScoringSegmentationBusinessException;
import coovitelCobranza.cobranzas.scoring.application.exception.ScoringSegmentationNotFoundException;
import coovitelCobranza.cobranzas.scoring.domain.model.ScoringSegmentation;
import coovitelCobranza.cobranzas.scoring.domain.repository.ScoringSegmentationRepository;
import coovitelCobranza.cobranzas.scoring.domain.service.ScoringRulesService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests - ScoringSegmentationApplicationService")
class ScoringSegmentationApplicationServiceTest {

    @Mock
    private ScoringSegmentationRepository repository;

    private ScoringSegmentationApplicationService service;

    @BeforeEach
    void setUp() {
        service = new ScoringSegmentationApplicationService(repository, new ScoringRulesService());
    }

    @Test
    @DisplayName("Calculate and store scoring")
    void calculateAndStore() {
        CalculateScoringRequest request = new CalculateScoringRequest(1L, 10L, 30, new BigDecimal("500000"), 2);
        ScoringSegmentation saved = ScoringSegmentation.reconstruct(
                100L, 1L, 10L, 42.0, "MEDIUM", "rules-v1", "High impact due to delinquency days", LocalDateTime.now()
        );

        when(repository.save(any(ScoringSegmentation.class))).thenReturn(saved);

        ScoringSegmentationResponse response = service.calculate(request);

        assertEquals(100L, response.id());
        assertEquals(1L, response.customerId());
        assertEquals(10L, response.obligationId());
    }

    @Test
    @DisplayName("Fails if customerId or obligationId are null")
    void validatesRequiredFields() {
        CalculateScoringRequest request = new CalculateScoringRequest(null, 10L, 0, BigDecimal.ZERO, 0);

        assertThrows(ScoringSegmentationBusinessException.class, () -> service.calculate(request));
    }

    @Test
    @DisplayName("Get by id throws not found when it does not exist")
    void getByIdNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ScoringSegmentationNotFoundException.class, () -> service.getById(1L));
    }

    @Test
    @DisplayName("List by customer returns history")
    void listByCustomer() {
        ScoringSegmentation a = ScoringSegmentation.reconstruct(
                1L, 5L, 10L, 20.0, "LOW", "rules-v1", "x", LocalDateTime.now()
        );
        ScoringSegmentation b = ScoringSegmentation.reconstruct(
                2L, 5L, 11L, 70.0, "HIGH", "rules-v1", "y", LocalDateTime.now()
        );

        when(repository.findByClientIdOrderByCreatedAtDesc(5L)).thenReturn(List.of(a, b));

        List<ScoringSegmentationResponse> result = service.listByCustomer(5L);
        assertEquals(2, result.size());
    }
}

