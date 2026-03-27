package cooviteCobranza.cobranzas.auditoria.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditoriaEventoJpaRepository extends JpaRepository<AuditoriaEventoJpaEntity, Long> {

    List<AuditoriaEventoJpaEntity> findByEntidadAndEntidadIdOrderByCreatedAtDesc(String entidad, Long entidadId);
}

