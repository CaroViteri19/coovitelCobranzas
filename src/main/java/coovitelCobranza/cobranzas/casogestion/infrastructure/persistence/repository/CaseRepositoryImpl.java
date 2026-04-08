package coovitelCobranza.cobranzas.casogestion.infrastructure.persistence.repository;

import coovitelCobranza.cobranzas.casogestion.domain.model.Case;
import coovitelCobranza.cobranzas.casogestion.domain.repository.CaseRepository;
import coovitelCobranza.cobranzas.casogestion.infrastructure.persistence.entity.CaseJpaEntity;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del repositorio de dominio CaseRepository usando JPA.
 *
 * Traduce operaciones del dominio a operaciones de persistencia JPA,
 * actuando como adaptador entre el dominio y la infraestructura.
 */
@Component
public class CaseRepositoryImpl implements CaseRepository {

    private final CaseJpaRepository jpaRepository;

    /**
     * Construye la implementación del repositorio.
     *
     * @param jpaRepository el repositorio JPA para acceder a la base de datos
     */
    public CaseRepositoryImpl(CaseJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    /**
     * Guarda un caso de cobranza en la base de datos.
     *
     * @param casoGestion el caso del dominio a guardar
     * @return el caso guardado con su ID asignado
     */
    @Override
    public Case save(Case casoGestion) {
        CaseJpaEntity entity = casoGestionToEntity(casoGestion);
        CaseJpaEntity savedEntity = jpaRepository.save(entity);
        return entityToCase(savedEntity);
    }

    /**
     * Busca un caso por su identificador.
     *
     * @param id el ID del caso
     * @return un Optional con el caso si existe
     */
    @Override
    public Optional<Case> findById(Long id) {
        return jpaRepository.findById(id).map(this::entityToCase);
    }

    /**
     * Encuentra todos los casos pendientes.
     *
     * @return lista de casos del dominio pendientes
     */
    @Override
    public List<Case> findPendientes() {
        return jpaRepository.findPendientes().stream()
                .map(this::entityToCase)
                .toList();
    }

    /**
     * Convierte un caso del dominio a una entidad JPA.
     *
     * @param casoGestion el caso del dominio
     * @return la entidad JPA
     */
    private CaseJpaEntity casoGestionToEntity(Case casoGestion) {
        return new CaseJpaEntity(
                casoGestion.getId(),
                casoGestion.getObligationId(),
                casoGestion.getPriority().name(),
                casoGestion.getStatus().name(),
                casoGestion.getAssignedAdvisor(),
                casoGestion.getNextActionAt(),
                casoGestion.getUpdatedAt()
        );
    }

    /**
     * Convierte una entidad JPA a un caso del dominio.
     *
     * @param entity la entidad JPA
     * @return el caso del dominio
     */
    private Case entityToCase(CaseJpaEntity entity) {
        return Case.reconstruct(
                entity.getId(),
                entity.getObligationId(),
                Case.Priority.valueOf(entity.getPriority()),
                Case.Status.valueOf(entity.getStatus()),
                entity.getAssignedAdvisor(),
                entity.getNextActionAt(),
                entity.getUpdatedAt()
        );
    }
}

