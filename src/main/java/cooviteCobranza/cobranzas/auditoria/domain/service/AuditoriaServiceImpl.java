package cooviteCobranza.cobranzas.auditoria.domain.service;

import cooviteCobranza.cobranzas.auditoria.domain.model.AuditoriaEvento;
import cooviteCobranza.cobranzas.auditoria.domain.repository.AuditoriaEventoRepository;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaServiceImpl implements AuditoriaService {

    private final AuditoriaEventoRepository repository;

    public AuditoriaServiceImpl(AuditoriaEventoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void registrarEvento(String entidad, Long entidadId, String accion, String usuario, String detalle) {
        AuditoriaEvento evento = AuditoriaEvento.crear(entidad, entidadId, accion, usuario, detalle);
        repository.save(evento);
    }
}

