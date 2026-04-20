package coovitelCobranza.cobranzas.policies.infrastructure.persistence.repository;

import coovitelCobranza.cobranzas.policies.infrastructure.persistence.entity.PolicyJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyJpaRepository extends JpaRepository<PolicyJpaEntity, Long> {
    List<PolicyJpaEntity> findByStrategyId(Long strategyId);
    List<PolicyJpaEntity> findByActivaTrue();
}

