package cooviteCobranza.cobranzas.orquestacion.infrastructure.persistence;

import cooviteCobranza.cobranzas.orquestacion.domain.model.OrquestacionEjecucion;
import cooviteCobranza.cobranzas.orquestacion.domain.repository.OrquestacionEjecucionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class OrquestacionEjecucionRepositoryImpl implements OrquestacionEjecucionRepository {

    private final OrquestacionEjecucionJpaRepository jpaRepository;

    public OrquestacionEjecucionRepositoryImpl(OrquestacionEjecucionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public OrquestacionEjecucion save(OrquestacionEjecucion ejecucion) {
        OrquestacionEjecucionJpaEntity entity = toEntity(ejecucion);
        OrquestacionEjecucionJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<OrquestacionEjecucion> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<OrquestacionEjecucion> findByCasoGestionId(Long casoGestionId) {
        return jpaRepository.findByCasoGestionId(casoGestionId).stream()
                .map(this::toDomain)
                .toList();
    }

    private OrquestacionEjecucionJpaEntity toEntity(OrquestacionEjecucion ejecucion) {
        return new OrquestacionEjecucionJpaEntity(
                ejecucion.getId(),
                ejecucion.getCasoGestionId(),
                ejecucion.getCanal(),
                ejecucion.getDestino(),
                ejecucion.getPlantilla(),
                ejecucion.getEstado().name(),
                ejecucion.getCreatedAt()
        );
    }

    private OrquestacionEjecucion toDomain(OrquestacionEjecucionJpaEntity entity) {
        return OrquestacionEjecucion.reconstruir(
                entity.getId(),
                entity.getCasoGestionId(),
                entity.getCanal(),
                entity.getDestino(),
                entity.getPlantilla(),
                OrquestacionEjecucion.Estado.valueOf(entity.getEstado()),
                entity.getCreatedAt()
        );
    }
}

