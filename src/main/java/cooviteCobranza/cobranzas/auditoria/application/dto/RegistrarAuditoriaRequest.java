package cooviteCobranza.cobranzas.auditoria.application.dto;

public record RegistrarAuditoriaRequest(
        String entidad,
        Long entidadId,
        String accion,
        String usuario,
        String detalle
) {
}

