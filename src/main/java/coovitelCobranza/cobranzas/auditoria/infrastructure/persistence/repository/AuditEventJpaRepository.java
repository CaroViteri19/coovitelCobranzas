package coovitelCobranza.cobranzas.auditoria.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import coovitelCobranza.cobranzas.auditoria.infrastructure.persistence.entity.AuditEventJpaEntity;

import java.util.List;

@Repository
public interface AuditEventJpaRepository extends JpaRepository<AuditEventJpaEntity, Long> {

    List<AuditEventJpaEntity> findByEntityAndEntityIdOrderByCreatedAtDesc(String entity, Long entityId);
}

