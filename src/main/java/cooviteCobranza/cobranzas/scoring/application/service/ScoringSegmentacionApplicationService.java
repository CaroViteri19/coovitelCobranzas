package cooviteCobranza.cobranzas.scoring.application.service;

import cooviteCobranza.cobranzas.scoring.application.dto.CalcularScoringRequest;
import cooviteCobranza.cobranzas.scoring.application.dto.ScoringSegmentacionResponse;
import cooviteCobranza.cobranzas.scoring.application.exception.ScoringSegmentacionBusinessException;
import cooviteCobranza.cobranzas.scoring.application.exception.ScoringSegmentacionNotFoundException;
import cooviteCobranza.cobranzas.scoring.domain.model.ScoringSegmentacion;
import cooviteCobranza.cobranzas.scoring.domain.repository.ScoringSegmentacionRepository;
import cooviteCobranza.cobranzas.scoring.domain.service.ReglasScoringService;
import cooviteCobranza.cobranzas.scoring.domain.service.ScoringService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ScoringSegmentacionApplicationService {

    private final ScoringSegmentacionRepository scoringRepository;
    private final ReglasScoringService reglasScoringService;

    public ScoringSegmentacionApplicationService(ScoringSegmentacionRepository scoringRepository,
                                                 ReglasScoringService reglasScoringService) {
        this.scoringRepository = scoringRepository;
        this.reglasScoringService = reglasScoringService;
    }

    @Transactional
    public ScoringSegmentacionResponse calcular(CalcularScoringRequest request) {
        try {
            if (request.clienteId() == null || request.obligacionId() == null) {
                throw new ScoringSegmentacionBusinessException("clienteId y obligacionId son requeridos");
            }

            ScoringService.ScoreResult result = reglasScoringService.calcularConDatos(
                    request.diasMora(),
                    request.saldoVencido(),
                    request.intentosContacto()
            );

            ScoringSegmentacion scoring = ScoringSegmentacion.crear(
                    request.clienteId(),
                    request.obligacionId(),
                    result.score(),
                    result.segmento(),
                    result.versionModelo(),
                    result.razonPrincipal()
            );

            ScoringSegmentacion saved = scoringRepository.save(scoring);
            return ScoringSegmentacionResponse.fromDomain(saved);
        } catch (ScoringSegmentacionBusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new ScoringSegmentacionBusinessException("Error al calcular scoring", e);
        }
    }

    @Transactional(readOnly = true)
    public ScoringSegmentacionResponse obtenerPorId(Long id) {
        ScoringSegmentacion scoring = scoringRepository.findById(id)
                .orElseThrow(() -> new ScoringSegmentacionNotFoundException(id));
        return ScoringSegmentacionResponse.fromDomain(scoring);
    }

    @Transactional(readOnly = true)
    public ScoringSegmentacionResponse obtenerUltimoPorObligacion(Long obligacionId) {
        ScoringSegmentacion scoring = scoringRepository.findTopByObligacionIdOrderByCreatedAtDesc(obligacionId)
                .orElseThrow(() -> new ScoringSegmentacionNotFoundException("obligacionId=" + obligacionId));
        return ScoringSegmentacionResponse.fromDomain(scoring);
    }

    @Transactional(readOnly = true)
    public List<ScoringSegmentacionResponse> listarPorCliente(Long clienteId) {
        return scoringRepository.findByClienteIdOrderByCreatedAtDesc(clienteId).stream()
                .map(ScoringSegmentacionResponse::fromDomain)
                .toList();
    }
}

