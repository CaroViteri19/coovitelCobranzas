package cooviteCobranza.cobranzas.politicas.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PoliticaJpaRepository extends JpaRepository<PoliticaJpaEntity, Long> {
    List<PoliticaJpaEntity> findByEstrategiaId(Long estrategiaId);
    List<PoliticaJpaEntity> findByActivaTrue();
}

