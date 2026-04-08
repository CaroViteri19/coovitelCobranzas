package coovitelCobranza.cobranzas.cliente.application.dto;

/**
 * Solicitud para crear un nuevo cliente.
 * Contiene los datos básicos necesarios para registrar un cliente en el sistema.
 */
public record CreateClientRequest(
        String tipoDocumento,
        String numeroDocumento,
        String fullName,
        String telefono,
        String email
) {
}

