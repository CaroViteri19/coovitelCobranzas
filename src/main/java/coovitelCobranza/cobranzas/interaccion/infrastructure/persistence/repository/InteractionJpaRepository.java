package coovitelCobranza.cobranzas.interaccion.infrastructure.persistence.repository;

import coovitelCobranza.cobranzas.interaccion.infrastructure.persistence.entity.InteractionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InteractionJpaRepository extends JpaRepository<InteractionJpaEntity, Long> {

    List<InteractionJpaEntity> findByCasoGestionId(Long casoGestionId);
}

