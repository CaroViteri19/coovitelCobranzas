package coovitelCobranza.cobranzas.casemanagement.infrastructure.persistence.repository;

import coovitelCobranza.cobranzas.casemanagement.domain.model.Case;
import coovitelCobranza.cobranzas.casemanagement.domain.repository.CaseRepository;
import coovitelCobranza.cobranzas.casemanagement.infrastructure.persistence.entity.CaseJpaEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementación del repositorio de dominio CaseRepository usando JPA.
 *
 * Traduce operaciones del dominio a operaciones de persistencia JPA,
 * actuando como adaptador entre el dominio y la infraestructura.
 */
@Component
public class CaseRepositoryImpl implements CaseRepository {

    private static final Logger log = LoggerFactory.getLogger(CaseRepositoryImpl.class);

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
        // La columna status en BD es VARCHAR, por eso enviamos los .name() del enum
        // y no instancias del enum (evita binding issues de Hibernate contra String).
        return jpaRepository.findByStatusIn(
                        List.of(Case.Status.NEW.name(), Case.Status.IN_MANAGEMENT.name()))
                .stream()
                .map(this::safeEntityToCase)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Variante tolerante de {@link #entityToCase}: si una fila tiene data
     * corrupta (p.ej. priority "BAJA" legacy o status inválido), se loguea
     * y se omite en vez de reventar toda la respuesta con 500.
     */
    private Case safeEntityToCase(CaseJpaEntity entity) {
        try {
            return entityToCase(entity);
        } catch (RuntimeException e) {
            log.warn("Skipping case id={} with invalid data: priority='{}', status='{}' → {}",
                    entity.getId(), entity.getPriority(), entity.getStatus(), e.getMessage());
            return null;
        }
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
                Case.priorityFromPersistence(entity.getPriority()),
                Case.statusFromPersistence(entity.getStatus()),
                entity.getAssignedAdvisor(),
                entity.getNextActionAt(),
                entity.getUpdatedAt()
        );
    }
}

