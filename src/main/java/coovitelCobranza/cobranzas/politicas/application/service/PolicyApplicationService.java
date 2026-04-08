package coovitelCobranza.cobranzas.politicas.application.service;

import coovitelCobranza.cobranzas.politicas.application.dto.CreatePolicyRequest;
import coovitelCobranza.cobranzas.politicas.application.dto.PolicyResponse;
import coovitelCobranza.cobranzas.politicas.application.exception.PoliciesBusinessException;
import coovitelCobranza.cobranzas.politicas.application.exception.PolicyNotFoundException;
import coovitelCobranza.cobranzas.politicas.application.exception.StrategyNotFoundException;
import coovitelCobranza.cobranzas.politicas.domain.model.Policy;
import coovitelCobranza.cobranzas.politicas.domain.repository.PolicyRepository;
import coovitelCobranza.cobranzas.politicas.domain.repository.StrategyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PolicyApplicationService {

    private final PolicyRepository policyRepository;
    private final StrategyRepository strategyRepository;

    public PolicyApplicationService(PolicyRepository policyRepository,
                                    StrategyRepository strategyRepository) {
        this.policyRepository = policyRepository;
        this.strategyRepository = strategyRepository;
    }

    @Transactional
    public PolicyResponse create(CreatePolicyRequest request) {
        try {
            strategyRepository.findById(request.strategyId())
                    .orElseThrow(() -> new StrategyNotFoundException(request.strategyId()));

            Policy.TipoCobro collectionType = Policy.TipoCobro.valueOf(request.collectionType());
            Policy policy = Policy.create(request.strategyId(), collectionType, request.action());
            policy.configureDelinquencyRange(request.minDelinquencyDays(), request.maxDelinquencyDays());

            Policy saved = policyRepository.save(policy);
            return PolicyResponse.fromDomain(saved);
        } catch (StrategyNotFoundException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw new PoliciesBusinessException("Invalid collection type: " + request.collectionType());
        } catch (Exception e) {
            throw new PoliciesBusinessException("Error creating policy", e);
        }
    }

    @Transactional(readOnly = true)
    public PolicyResponse getById(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new PolicyNotFoundException(id));
        return PolicyResponse.fromDomain(policy);
    }

    @Transactional(readOnly = true)
    public List<PolicyResponse> listByStrategy(Long strategyId) {
        return policyRepository.findByStrategyId(strategyId).stream()
                .map(PolicyResponse::fromDomain)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PolicyResponse> listActive() {
        return policyRepository.findActivas().stream()
                .map(PolicyResponse::fromDomain)
                .toList();
    }

    @Transactional
    public PolicyResponse activate(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new PolicyNotFoundException(id));
        policy.activate();
        return PolicyResponse.fromDomain(policyRepository.save(policy));
    }

    @Transactional
    public PolicyResponse deactivate(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new PolicyNotFoundException(id));
        policy.deactivate();
        return PolicyResponse.fromDomain(policyRepository.save(policy));
    }
}
