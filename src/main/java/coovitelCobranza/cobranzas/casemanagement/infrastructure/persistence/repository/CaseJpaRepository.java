package coovitelCobranza.cobranzas.casemanagement.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
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
     * Encuentra todos los casos cuyo estado esté incluido en la lista dada.
     *
     * La columna {@code status} se persiste como {@code VARCHAR}, por lo que este
     * método acepta directamente los nombres del enum {@code Case.Status} en
     * formato {@code String} (p. ej. "NEW", "IN_MANAGEMENT").
     *
     * @param statuses lista de estados (como String) a filtrar
     * @return lista de entidades cuyo estado coincide
     */
    List<CaseJpaEntity> findByStatusIn(List<String> statuses);
}

