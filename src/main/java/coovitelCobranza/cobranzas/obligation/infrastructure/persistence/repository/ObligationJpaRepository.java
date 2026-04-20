package coovitelCobranza.cobranzas.obligation.infrastructure.persistence.repository;

import coovitelCobranza.cobranzas.obligation.infrastructure.persistence.entity.ObligationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ObligationJpaRepository extends JpaRepository<ObligationJpaEntity, Long> {

    Optional<ObligationJpaEntity> findByObligationNumber(String obligationNumber);

    List<ObligationJpaEntity> findByCustomerId(Long customerId);
}

