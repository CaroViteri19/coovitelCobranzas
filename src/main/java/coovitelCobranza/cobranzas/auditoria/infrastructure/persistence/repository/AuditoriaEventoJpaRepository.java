package coovitelCobranza.cobranzas.auditoria.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import coovitelCobranza.cobranzas.auditoria.infrastructure.persistence.entity.AuditoriaEventoJpaEntity;

import java.util.List;

@Repository
public interface AuditoriaEventoJpaRepository extends JpaRepository<AuditoriaEventoJpaEntity, Long> {

    List<AuditoriaEventoJpaEntity> findByEntidadAndEntidadIdOrderByCreatedAtDesc(String entidad, Long entidadId);
}

