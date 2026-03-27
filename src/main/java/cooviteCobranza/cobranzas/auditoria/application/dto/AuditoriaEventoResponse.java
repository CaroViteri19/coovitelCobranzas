package cooviteCobranza.cobranzas.auditoria.application.dto;

import cooviteCobranza.cobranzas.auditoria.domain.model.AuditoriaEvento;

import java.time.LocalDateTime;

public record AuditoriaEventoResponse(
        Long id,
        String entidad,
        Long entidadId,
        String accion,
        String usuario,
        String detalle,
        LocalDateTime createdAt
) {

    public static AuditoriaEventoResponse fromDomain(AuditoriaEvento evento) {
        return new AuditoriaEventoResponse(
                evento.getId(),
                evento.getEntidad(),
                evento.getEntidadId(),
                evento.getAccion(),
                evento.getUsuario(),
                evento.getDetalle(),
                evento.getCreatedAt()
        );
    }
}

