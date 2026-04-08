package coovitelCobranza.cobranzas.casogestion.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import coovitelCobranza.cobranzas.casogestion.infrastructure.persistence.entity.CaseJpaEntity;

import java.util.List;

/**
 * Repositorio Spring Data JPA para la persistencia de casos de cobranza.
 *
 * Proporciona operaciones CRUD estándar y consultas personalizadas
 * para acceder a casos en la base de datos.
 */
@Repository
public interface CaseJpaRepository extends JpaRepository<CaseJpaEntity, Long> {

    /**
     * Encuentra todos los casos con estado pendiente de gestión.
     *
     * @return lista de casos con estado ABIERTO o EN_GESTION
     */
    @Query("SELECT c FROM CaseJpaEntity c WHERE c.estado IN ('ABIERTO', 'EN_GESTION')")
    List<CaseJpaEntity> findPendientes();
}

