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
    public AuditEvent save(AuditEvent event) {
        AuditEventJpaEntity entity = toEntity(event);
        AuditEventJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<AuditEvent> findByEntidadAndEntidadIdOrderByCreatedAtDesc(String entity, Long entityId) {
        return jpaRepository.findByEntityAndEntityIdOrderByCreatedAtDesc(entity, entityId).stream()
                .map(this::toDomain)
                .toList();
    }

    private AuditEventJpaEntity toEntity(AuditEvent event) {
        return new AuditEventJpaEntity(
                event.getId(),
                event.getEntityType(),
                event.getEntityId(),
                event.getAction(),
                event.getUser(),
                event.getUserRole(),
                event.getSource(),
                event.getModule(),
                event.getCorrelationId(),
                event.getDetails(),
                event.getCreatedAt()
        );
    }

    private AuditEvent toDomain(AuditEventJpaEntity entity) {
        return AuditEvent.reconstruct(
                entity.getId(),
                entity.getEntity(),
                entity.getEntityId(),
                entity.getAction(),
                entity.getUser(),
                entity.getUserRole(),
                entity.getSource(),
                entity.getModule(),
                entity.getCorrelationId(),
                entity.getDetail(),
                entity.getCreatedAt()
        );
    }
}
