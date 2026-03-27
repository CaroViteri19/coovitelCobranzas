package coovitelCobranza.cobranzas.auditoria.domain.service;

public interface AuditoriaService {

    void registrarEvento(String entidad, Long entidadId, String accion, String usuario, String detalle);
}

