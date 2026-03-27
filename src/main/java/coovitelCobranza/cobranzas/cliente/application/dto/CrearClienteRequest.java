package coovitelCobranza.cobranzas.cliente.application.dto;

public record CrearClienteRequest(
        String tipoDocumento,
        String numeroDocumento,
        String nombreCompleto,
        String telefono,
        String email
) {
}

