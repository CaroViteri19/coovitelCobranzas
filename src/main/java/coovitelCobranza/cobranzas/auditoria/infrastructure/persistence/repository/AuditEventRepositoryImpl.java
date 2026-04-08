package coovitelCobranza.cobranzas.auditoria.infrastructure.persistence.repository;

import coovitelCobranza.cobranzas.auditoria.domain.model.AuditEvent;
import coovitelCobranza.cobranzas.auditoria.domain.repository.AuditEventRepository;
import coovitelCobranza.cobranzas.auditoria.infrastructure.persistence.entity.AuditEventJpaEntity;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuditEventRepositoryImpl implements AuditEventRepository {

    private final AuditEventJpaRepository jpaRepository;

    public AuditEventRepositoryImpl(AuditEventJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public AuditEvent save(AuditEvent evento) {
        AuditEventJpaEntity entity = toEntity(evento);
        AuditEventJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<AuditEvent> findByEntidadAndEntidadIdOrderByCreatedAtDesc(String entity, Long entityId) {
        return jpaRepository.findByEntityAndEntityIdOrderByCreatedAtDesc(entity, entityId).stream()
                .map(this::toDomain)
                .toList();
    }

    private AuditEventJpaEntity toEntity(AuditEvent evento) {
        return new AuditEventJpaEntity(
                evento.getId(),
                evento.getEntidad(),
                evento.getEntidadId(),
                evento.getAction(),
                evento.getUsuario(),
                evento.getDetalle(),
                evento.getCreatedAt()
        );
    }

    private AuditEvent toDomain(AuditEventJpaEntity entity) {
        return AuditEvent.reconstruct(
                entity.getId(),
                entity.getEntidad(),
                entity.getEntidadId(),
                entity.getAction(),
                entity.getUsuario(),
                entity.getDetalle(),
                entity.getCreatedAt()
        );
    }
}

