package coovitelCobranza.cobranzas.cliente.application.dto;

public record CreateClientRequest(
        String tipoDocumento,
        String numeroDocumento,
        String fullName,
        String telefono,
        String email
) {
}

