package coovitelCobranza.cobranzas.politicas.infrastructure.persistence.repository;

import coovitelCobranza.cobranzas.politicas.infrastructure.persistence.entity.StrategyJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StrategyJpaRepository extends JpaRepository<StrategyJpaEntity, Long> {
    Optional<StrategyJpaEntity> findByName(String name);
    List<StrategyJpaEntity> findByActivaTrue();
}

