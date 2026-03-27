package coovitelCobranza.cobranzas.casogestion.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import coovitelCobranza.cobranzas.casogestion.infrastructure.persistence.entity.CasoGestionJpaEntity;

import java.util.List;

@Repository
public interface CasoGestionJpaRepository extends JpaRepository<CasoGestionJpaEntity, Long> {

    @Query("SELECT c FROM CasoGestionJpaEntity c WHERE c.estado IN ('ABIERTO', 'EN_GESTION')")
    List<CasoGestionJpaEntity> findPendientes();
}

