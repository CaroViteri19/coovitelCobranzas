package cooviteCobranza.cobranzas.orquestacion.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrquestacionEjecucionJpaRepository extends JpaRepository<OrquestacionEjecucionJpaEntity, Long> {

    List<OrquestacionEjecucionJpaEntity> findByCasoGestionId(Long casoGestionId);
}

