package coovitelCobranza.cobranzas.audit.infrastructure.persistence.repository;

import coovitelCobranza.cobranzas.audit.domain.model.AuditEvent;
import coovitelCobranza.cobranzas.audit.domain.repository.AuditEventRepository;
import coovitelCobranza.cobranzas.audit.infrastructure.persistence.entity.AuditEventJpaEntity;

import org.springframework.stereotype.Component;

import java.util.Comparator;
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
    public List<AuditEvent> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(String entityType, Long entityId) {
        return jpaRepository.findAll().stream()
                .filter(entity -> entityType.equals(entity.getEntityType()) && entityId.equals(entity.getEntityId()))
                .sorted(Comparator.comparing(AuditEventJpaEntity::getCreatedAt).reversed())
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
                entity.getEntityType(),
                entity.getEntityId(),
                entity.getAction(),
                entity.getUsername(),
                entity.getUserRole(),
                entity.getSource(),
                entity.getModule(),
                entity.getCorrelationId(),
                entity.getDetails(),
                entity.getCreatedAt()
        );
    }
}
