package coovitelCobranza.cobranzas.cliente.application.dto;

/**
 * Solicitud alternativa para actualizar la información de contacto de un cliente.
 * Permite modificar el teléfono y correo electrónico sin incluir el ID.
 */
public record UpdateContactClientRequest(
        String telefono,
        String email
) {
}

