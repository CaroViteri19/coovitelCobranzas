package coovitelCobranza.cobranzas.audit.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import coovitelCobranza.cobranzas.audit.infrastructure.persistence.entity.AuditEventJpaEntity;

@Repository
public interface AuditEventJpaRepository extends JpaRepository<AuditEventJpaEntity, Long> {
}

