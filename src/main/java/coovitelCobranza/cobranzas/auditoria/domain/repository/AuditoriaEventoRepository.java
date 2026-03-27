package coovitelCobranza.cobranzas.auditoria.domain.repository;

import coovitelCobranza.cobranzas.auditoria.domain.model.AuditoriaEvento;

import java.util.List;

public interface AuditoriaEventoRepository {

    AuditoriaEvento save(AuditoriaEvento evento);

    List<AuditoriaEvento> findByEntidadAndEntidadIdOrderByCreatedAtDesc(String entidad, Long entidadId);
}

