package coovitelCobranza.cobranzas.policies.infrastructure.persistence.repository;

import coovitelCobranza.cobranzas.policies.infrastructure.persistence.entity.StrategyJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StrategyJpaRepository extends JpaRepository<StrategyJpaEntity, Long> {
    Optional<StrategyJpaEntity> findByName(String name);
    List<StrategyJpaEntity> findByActivaTrue();
}

