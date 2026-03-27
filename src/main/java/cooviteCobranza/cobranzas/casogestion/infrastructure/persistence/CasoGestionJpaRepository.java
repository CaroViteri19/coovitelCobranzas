package cooviteCobranza.cobranzas.casogestion.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CasoGestionJpaRepository extends JpaRepository<CasoGestionJpaEntity, Long> {

    @Query("SELECT c FROM CasoGestionJpaEntity c WHERE c.estado IN ('ABIERTO', 'EN_GESTION')")
    List<CasoGestionJpaEntity> findPendientes();
}

