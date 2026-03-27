package cooviteCobranza.cobranzas.politicas.infrastructure.persistence;

import cooviteCobranza.cobranzas.politicas.domain.model.Estrategia;
import cooviteCobranza.cobranzas.politicas.domain.repository.EstrategiaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class EstrategiaRepositoryImpl implements EstrategiaRepository {

    private final EstrategiaJpaRepository jpaRepository;

    public EstrategiaRepositoryImpl(EstrategiaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Estrategia save(Estrategia estrategia) {
        EstrategiaJpaEntity entity = toEntity(estrategia);
        EstrategiaJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Estrategia> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Estrategia> findActivas() {
        return jpaRepository.findByActivaTrue().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Estrategia> findByNombre(String nombre) {
        return jpaRepository.findByNombre(nombre).map(this::toDomain);
    }

    private EstrategiaJpaEntity toEntity(Estrategia estrategia) {
        return new EstrategiaJpaEntity(
                estrategia.getId(),
                estrategia.getNombre(),
                estrategia.getDescripcion(),
                estrategia.isActiva(),
                estrategia.getMaxIntentosContacto(),
                estrategia.getDiasAntesDeeEscalacion(),
                estrategia.getRolAsignacionEscalada(),
                estrategia.getUpdatedAt()
        );
    }

    private Estrategia toDomain(EstrategiaJpaEntity entity) {
        return Estrategia.reconstruir(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.isActiva(),
                entity.getMaxIntentosContacto(),
                entity.getDiasAntesDeeEscalacion(),
                entity.getRolAsignacionEscalada(),
                entity.getUpdatedAt()
        );
    }
}

