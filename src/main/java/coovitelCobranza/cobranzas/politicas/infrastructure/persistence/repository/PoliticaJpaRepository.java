package coovitelCobranza.cobranzas.politicas.infrastructure.persistence.repository;

import coovitelCobranza.cobranzas.politicas.infrastructure.persistence.entity.PoliticaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PoliticaJpaRepository extends JpaRepository<PoliticaJpaEntity, Long> {
    List<PoliticaJpaEntity> findByEstrategiaId(Long estrategiaId);
    List<PoliticaJpaEntity> findByActivaTrue();
}

