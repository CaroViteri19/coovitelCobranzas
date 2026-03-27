package cooviteCobranza.cobranzas.obligacion.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ObligacionJpaRepository extends JpaRepository<ObligacionJpaEntity, Long> {

    Optional<ObligacionJpaEntity> findByObligationNumber(String obligationNumber);

    List<ObligacionJpaEntity> findByCustomerId(Long customerId);
}

