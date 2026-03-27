package coovitelCobranza.cobranzas.casogestion.infrastructure.persistence.repository;

import coovitelCobranza.cobranzas.casogestion.domain.model.CasoGestion;
import coovitelCobranza.cobranzas.casogestion.domain.repository.CasoGestionRepository;
import coovitelCobranza.cobranzas.casogestion.infrastructure.persistence.entity.CasoGestionJpaEntity;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CasoGestionRepositoryImpl implements CasoGestionRepository {

    private final CasoGestionJpaRepository jpaRepository;

    public CasoGestionRepositoryImpl(CasoGestionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public CasoGestion save(CasoGestion casoGestion) {
        CasoGestionJpaEntity entity = casoGestionToEntity(casoGestion);
        CasoGestionJpaEntity savedEntity = jpaRepository.save(entity);
        return entityToCasoGestion(savedEntity);
    }

    @Override
    public Optional<CasoGestion> findById(Long id) {
        return jpaRepository.findById(id).map(this::entityToCasoGestion);
    }

    @Override
    public List<CasoGestion> findPendientes() {
        return jpaRepository.findPendientes().stream()
                .map(this::entityToCasoGestion)
                .toList();
    }

    // Mappers
    private CasoGestionJpaEntity casoGestionToEntity(CasoGestion casoGestion) {
        return new CasoGestionJpaEntity(
                casoGestion.getId(),
                casoGestion.getObligacionId(),
                casoGestion.getPrioridad().name(),
                casoGestion.getEstado().name(),
                casoGestion.getAsesorAsignado(),
                casoGestion.getProximaAccionAt(),
                casoGestion.getUpdatedAt()
        );
    }

    private CasoGestion entityToCasoGestion(CasoGestionJpaEntity entity) {
        return CasoGestion.reconstruir(
                entity.getId(),
                entity.getObligacionId(),
                CasoGestion.Prioridad.valueOf(entity.getPrioridad()),
                CasoGestion.Estado.valueOf(entity.getEstado()),
                entity.getAsesorAsignado(),
                entity.getProximaAccionAt(),
                entity.getUpdatedAt()
        );
    }
}

