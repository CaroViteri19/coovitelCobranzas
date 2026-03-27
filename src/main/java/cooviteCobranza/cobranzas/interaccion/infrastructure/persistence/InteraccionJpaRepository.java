package cooviteCobranza.cobranzas.interaccion.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InteraccionJpaRepository extends JpaRepository<InteraccionJpaEntity, Long> {

    List<InteraccionJpaEntity> findByCasoGestionId(Long casoGestionId);
}

