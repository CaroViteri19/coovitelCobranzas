package coovitelCobranza.cobranzas.scoring.application.service;

import coovitelCobranza.cobranzas.scoring.application.dto.CalculateScoringRequest;
import coovitelCobranza.cobranzas.scoring.application.dto.ScoringSegmentationResponse;
import coovitelCobranza.cobranzas.scoring.application.exception.ScoringSegmentationBusinessException;
import coovitelCobranza.cobranzas.scoring.application.exception.ScoringSegmentationNotFoundException;
import coovitelCobranza.cobranzas.scoring.domain.model.ScoringSegmentation;
import coovitelCobranza.cobranzas.scoring.domain.repository.ScoringSegmentationRepository;
import coovitelCobranza.cobranzas.scoring.domain.service.ScoringRulesService;
import coovitelCobranza.cobranzas.scoring.domain.service.ScoringService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ScoringSegmentationApplicationService {

    private final ScoringSegmentationRepository scoringRepository;
    private final ScoringRulesService scoringRulesService;

    public ScoringSegmentationApplicationService(ScoringSegmentationRepository scoringRepository,
                                                 ScoringRulesService scoringRulesService) {
        this.scoringRepository = scoringRepository;
        this.scoringRulesService = scoringRulesService;
    }

    @Transactional
    public ScoringSegmentationResponse calculate(CalculateScoringRequest request) {
        try {
            if (request.customerId() == null || request.obligationId() == null) {
                throw new ScoringSegmentationBusinessException("customerId and obligationId are required");
            }

            ScoringService.ScoreResult result = scoringRulesService.calculateWithData(
                    request.delinquencyDays(),
                    request.overdueBalance(),
                    request.contactAttempts()
            );

            ScoringSegmentation scoring = ScoringSegmentation.create(
                    request.customerId(),
                    request.obligationId(),
                    result.score(),
                    result.segment(),
                    result.modelVersion(),
                    result.mainReason()
            );

            ScoringSegmentation saved = scoringRepository.save(scoring);
            return ScoringSegmentationResponse.fromDomain(saved);
        } catch (ScoringSegmentationBusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new ScoringSegmentationBusinessException("Error calculating scoring", e);
        }
    }

    @Transactional(readOnly = true)
    public ScoringSegmentationResponse getById(Long id) {
        ScoringSegmentation scoring = scoringRepository.findById(id)
                .orElseThrow(() -> new ScoringSegmentationNotFoundException(id));
        return ScoringSegmentationResponse.fromDomain(scoring);
    }

    @Transactional(readOnly = true)
    public ScoringSegmentationResponse getLatestByObligation(Long obligationId) {
        ScoringSegmentation scoring = scoringRepository.findTopByObligationIdOrderByCreatedAtDesc(obligationId)
                .orElseThrow(() -> new ScoringSegmentationNotFoundException("obligationId=" + obligationId));
        return ScoringSegmentationResponse.fromDomain(scoring);
    }

    @Transactional(readOnly = true)
    public List<ScoringSegmentationResponse> listByCustomer(Long customerId) {
        return scoringRepository.findByClientIdOrderByCreatedAtDesc(customerId).stream()
                .map(ScoringSegmentationResponse::fromDomain)
                .toList();
    }
}
