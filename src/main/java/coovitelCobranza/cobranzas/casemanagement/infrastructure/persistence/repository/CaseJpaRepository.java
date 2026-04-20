package coovitelCobranza.cobranzas.casemanagement.infrastructure.persistence.repository;

import coovitelCobranza.cobranzas.casemanagement.domain.model.Case;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import coovitelCobranza.cobranzas.casemanagement.infrastructure.persistence.entity.CaseJpaEntity;

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
    List<CaseJpaEntity> findPendientesByStatusIn(List<Case.Status> statuses);
}

