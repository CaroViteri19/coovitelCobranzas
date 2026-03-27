package coovitelCobranza.cobranzas.politicas.infrastructure.persistence.repository;

import coovitelCobranza.cobranzas.politicas.infrastructure.persistence.entity.EstrategiaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstrategiaJpaRepository extends JpaRepository<EstrategiaJpaEntity, Long> {
    Optional<EstrategiaJpaEntity> findByNombre(String nombre);
    List<EstrategiaJpaEntity> findByActivaTrue();
}

