package cooviteCobranza.cobranzas.interaccion.infrastructure.persistence;

import cooviteCobranza.cobranzas.interaccion.domain.model.Interaccion;
import cooviteCobranza.cobranzas.interaccion.domain.repository.InteraccionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class InteraccionRepositoryImpl implements InteraccionRepository {

    private final InteraccionJpaRepository jpaRepository;

    public InteraccionRepositoryImpl(InteraccionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Interaccion save(Interaccion interaccion) {
        InteraccionJpaEntity entity = interaccionToEntity(interaccion);
        InteraccionJpaEntity savedEntity = jpaRepository.save(entity);
        return entityToInteraccion(savedEntity);
    }

    @Override
    public Optional<Interaccion> findById(Long id) {
        return jpaRepository.findById(id).map(this::entityToInteraccion);
    }

    @Override
    public List<Interaccion> findByCasoGestionId(Long casoGestionId) {
        return jpaRepository.findByCasoGestionId(casoGestionId).stream()
                .map(this::entityToInteraccion)
                .toList();
    }

    // Mappers
    private InteraccionJpaEntity interaccionToEntity(Interaccion interaccion) {
        return new InteraccionJpaEntity(
                interaccion.getId(),
                interaccion.getCasoGestionId(),
                interaccion.getCanal().name(),
                interaccion.getPlantilla(),
                interaccion.getResultado().name(),
                interaccion.getCreatedAt()
        );
    }

    private Interaccion entityToInteraccion(InteraccionJpaEntity entity) {
        return Interaccion.reconstruir(
                entity.getId(),
                entity.getCasoGestionId(),
                Interaccion.Canal.valueOf(entity.getCanal()),
                entity.getPlantilla(),
                Interaccion.EstadoResultado.valueOf(entity.getResultado()),
                entity.getCreatedAt()
        );
    }
}

