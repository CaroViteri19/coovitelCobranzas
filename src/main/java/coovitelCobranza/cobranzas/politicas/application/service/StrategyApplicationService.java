package coovitelCobranza.cobranzas.politicas.application.service;

import coovitelCobranza.cobranzas.politicas.application.dto.CreateStrategyRequest;
import coovitelCobranza.cobranzas.politicas.application.dto.StrategyResponse;
import coovitelCobranza.cobranzas.politicas.application.exception.PoliciesBusinessException;
import coovitelCobranza.cobranzas.politicas.application.exception.StrategyNotFoundException;
import coovitelCobranza.cobranzas.politicas.domain.model.Strategy;
import coovitelCobranza.cobranzas.politicas.domain.repository.StrategyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StrategyApplicationService {

    private final StrategyRepository strategyRepository;

    public StrategyApplicationService(StrategyRepository strategyRepository) {
        this.strategyRepository = strategyRepository;
    }

    @Transactional
    public StrategyResponse create(CreateStrategyRequest request) {
        try {
            var existing = strategyRepository.findByNombre(request.name());
            if (existing.isPresent()) {
                throw new PoliciesBusinessException("Strategy already exists with name: " + request.name());
            }

            Strategy strategy = Strategy.create(request.name(), request.description());
            strategy.configureParameters(
                    request.maxContactAttempts(),
                    request.daysBeforeEscalation(),
                    request.escalationAssignmentRole()
            );

            Strategy saved = strategyRepository.save(strategy);
            return StrategyResponse.fromDomain(saved);
        } catch (PoliciesBusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new PoliciesBusinessException("Error creating strategy", e);
        }
    }

    @Transactional(readOnly = true)
    public StrategyResponse getById(Long id) {
        Strategy strategy = strategyRepository.findById(id)
                .orElseThrow(() -> new StrategyNotFoundException(id));
        return StrategyResponse.fromDomain(strategy);
    }

    @Transactional(readOnly = true)
    public List<StrategyResponse> listActive() {
        return strategyRepository.findActivas().stream()
                .map(StrategyResponse::fromDomain)
                .toList();
    }

    @Transactional
    public StrategyResponse activate(Long id) {
        Strategy strategy = strategyRepository.findById(id)
                .orElseThrow(() -> new StrategyNotFoundException(id));
        strategy.activate();
        return StrategyResponse.fromDomain(strategyRepository.save(strategy));
    }

    @Transactional
    public StrategyResponse deactivate(Long id) {
        Strategy strategy = strategyRepository.findById(id)
                .orElseThrow(() -> new StrategyNotFoundException(id));
        strategy.deactivate();
        return StrategyResponse.fromDomain(strategyRepository.save(strategy));
    }
}
