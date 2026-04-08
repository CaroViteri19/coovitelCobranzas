package coovitelCobranza.cobranzas.obligacion.application.service;

import coovitelCobranza.cobranzas.obligacion.application.dto.ObligationResponse;
import coovitelCobranza.cobranzas.obligacion.application.exception.ObligationBusinessException;
import coovitelCobranza.cobranzas.obligacion.application.exception.ObligationNotFoundException;
import coovitelCobranza.cobranzas.obligacion.domain.model.Obligation;
import coovitelCobranza.cobranzas.obligacion.domain.repository.ObligationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ObligationApplicationService {

    private final ObligationRepository obligationRepository;

    public ObligationApplicationService(ObligationRepository obligationRepository) {
        this.obligationRepository = obligationRepository;
    }

    @Transactional(readOnly = true)
    public ObligationResponse getById(Long id) {
        Obligation obligation = obligationRepository.findById(id)
                .orElseThrow(() -> new ObligationNotFoundException(id));
        return ObligationResponse.fromDomain(obligation);
    }

    @Transactional(readOnly = true)
    public ObligationResponse getByNumber(String obligationNumber) {
        Obligation obligation = obligationRepository.findByObligationNumber(obligationNumber)
                .orElseThrow(() -> new ObligationNotFoundException(obligationNumber));
        return ObligationResponse.fromDomain(obligation);
    }

    @Transactional(readOnly = true)
    public List<ObligationResponse> listByCustomer(Long customerId) {
        return obligationRepository.findByCustomerId(customerId).stream()
                .map(ObligationResponse::fromDomain)
                .toList();
    }

    @Transactional
    public ObligationResponse registerDelinquency(Long id, int delinquencyDays, BigDecimal overdueBalance) {
        Obligation obligation = obligationRepository.findById(id)
                .orElseThrow(() -> new ObligationNotFoundException(id));

        try {
            obligation.registerDelinquency(delinquencyDays, overdueBalance);
        } catch (IllegalArgumentException exception) {
            throw new ObligationBusinessException(exception.getMessage());
        }

        Obligation saved = obligationRepository.save(obligation);
        return ObligationResponse.fromDomain(saved);
    }

    @Transactional
    public ObligationResponse applyPayment(Long id, BigDecimal paymentAmount) {
        Obligation obligation = obligationRepository.findById(id)
                .orElseThrow(() -> new ObligationNotFoundException(id));

        try {
            obligation.applyPayment(paymentAmount);
        } catch (IllegalArgumentException exception) {
            throw new ObligationBusinessException(exception.getMessage());
        }

        Obligation saved = obligationRepository.save(obligation);
        return ObligationResponse.fromDomain(saved);
    }
}

