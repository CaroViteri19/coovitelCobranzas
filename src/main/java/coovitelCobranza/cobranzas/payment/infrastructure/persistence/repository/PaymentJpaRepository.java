package coovitelCobranza.cobranzas.payment.infrastructure.persistence.repository;

import coovitelCobranza.cobranzas.payment.infrastructure.persistence.entity.PaymentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentJpaRepository extends JpaRepository<PaymentJpaEntity, Long> {

    Optional<PaymentJpaEntity> findByExternalReference(String externalReference);

    List<PaymentJpaEntity> findByObligationId(Long obligationId);
}

