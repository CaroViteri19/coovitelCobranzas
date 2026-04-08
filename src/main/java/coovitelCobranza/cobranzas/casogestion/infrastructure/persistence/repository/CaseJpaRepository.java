package coovitelCobranza.cobranzas.casogestion.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import coovitelCobranza.cobranzas.casogestion.infrastructure.persistence.entity.CaseJpaEntity;

import java.util.List;

@Repository
public interface CaseJpaRepository extends JpaRepository<CaseJpaEntity, Long> {

    @Query("SELECT c FROM CaseJpaEntity c WHERE c.estado IN ('ABIERTO', 'EN_GESTION')")
    List<CaseJpaEntity> findPendientes();
}

