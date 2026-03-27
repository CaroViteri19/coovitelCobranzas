package cooviteCobranza.cobranzas.auditoria.infrastructure.persistence;

import cooviteCobranza.cobranzas.auditoria.domain.model.AuditoriaEvento;
import cooviteCobranza.cobranzas.auditoria.domain.repository.AuditoriaEventoRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuditoriaEventoRepositoryImpl implements AuditoriaEventoRepository {

    private final AuditoriaEventoJpaRepository jpaRepository;

    public AuditoriaEventoRepositoryImpl(AuditoriaEventoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public AuditoriaEvento save(AuditoriaEvento evento) {
        AuditoriaEventoJpaEntity entity = toEntity(evento);
        AuditoriaEventoJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<AuditoriaEvento> findByEntidadAndEntidadIdOrderByCreatedAtDesc(String entidad, Long entidadId) {
        return jpaRepository.findByEntidadAndEntidadIdOrderByCreatedAtDesc(entidad, entidadId).stream()
                .map(this::toDomain)
                .toList();
    }

    private AuditoriaEventoJpaEntity toEntity(AuditoriaEvento evento) {
        return new AuditoriaEventoJpaEntity(
                evento.getId(),
                evento.getEntidad(),
                evento.getEntidadId(),
                evento.getAccion(),
                evento.getUsuario(),
                evento.getDetalle(),
                evento.getCreatedAt()
        );
    }

    private AuditoriaEvento toDomain(AuditoriaEventoJpaEntity entity) {
        return AuditoriaEvento.reconstruir(
                entity.getId(),
                entity.getEntidad(),
                entity.getEntidadId(),
                entity.getAccion(),
                entity.getUsuario(),
                entity.getDetalle(),
                entity.getCreatedAt()
        );
    }
}

