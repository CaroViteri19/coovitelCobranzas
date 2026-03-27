package coovitelCobranza.cobranzas.scoring.application.service;

import coovitelCobranza.cobranzas.scoring.application.dto.CalculateScoringRequest;
import coovitelCobranza.cobranzas.scoring.application.dto.ScoringSegmentationResponse;
import coovitelCobranza.cobranzas.scoring.application.exception.ScoringSegmentationBusinessException;
import coovitelCobranza.cobranzas.scoring.application.exception.ScoringSegmentationNotFoundException;
import coovitelCobranza.cobranzas.scoring.domain.model.ScoringSegmentacion;
import coovitelCobranza.cobranzas.scoring.domain.repository.ScoringSegmentacionRepository;
import coovitelCobranza.cobranzas.scoring.domain.service.ReglasScoringService;
import coovitelCobranza.cobranzas.scoring.domain.service.ScoringService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ScoringSegmentationApplicationService {

    private final ScoringSegmentacionRepository scoringRepository;
    private final ReglasScoringService reglasScoringService;

    public ScoringSegmentationApplicationService(ScoringSegmentacionRepository scoringRepository,
                                                 ReglasScoringService reglasScoringService) {
        this.scoringRepository = scoringRepository;
        this.reglasScoringService = reglasScoringService;
    }

    @Transactional
    public ScoringSegmentationResponse calculate(CalculateScoringRequest request) {
        try {
            if (request.customerId() == null || request.obligationId() == null) {
                throw new ScoringSegmentationBusinessException("customerId and obligationId are required");
            }

            ScoringService.ScoreResult result = reglasScoringService.calcularConDatos(
                    request.delinquencyDays(),
                    request.overdueBalance(),
                    request.contactAttempts()
            );

            ScoringSegmentacion scoring = ScoringSegmentacion.crear(
                    request.customerId(),
                    request.obligationId(),
                    result.score(),
                    result.segmento(),
                    result.versionModelo(),
                    result.razonPrincipal()
            );

            ScoringSegmentacion saved = scoringRepository.save(scoring);
            return ScoringSegmentationResponse.fromDomain(saved);
        } catch (ScoringSegmentationBusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new ScoringSegmentationBusinessException("Error calculating scoring", e);
        }
    }

    @Transactional(readOnly = true)
    public ScoringSegmentationResponse getById(Long id) {
        ScoringSegmentacion scoring = scoringRepository.findById(id)
                .orElseThrow(() -> new ScoringSegmentationNotFoundException(id));
        return ScoringSegmentationResponse.fromDomain(scoring);
    }

    @Transactional(readOnly = true)
    public ScoringSegmentationResponse getLatestByObligation(Long obligationId) {
        ScoringSegmentacion scoring = scoringRepository.findTopByObligacionIdOrderByCreatedAtDesc(obligationId)
                .orElseThrow(() -> new ScoringSegmentationNotFoundException("obligationId=" + obligationId));
        return ScoringSegmentationResponse.fromDomain(scoring);
    }

    @Transactional(readOnly = true)
    public List<ScoringSegmentationResponse> listByCustomer(Long customerId) {
        return scoringRepository.findByClienteIdOrderByCreatedAtDesc(customerId).stream()
                .map(ScoringSegmentationResponse::fromDomain)
                .toList();
    }
}

