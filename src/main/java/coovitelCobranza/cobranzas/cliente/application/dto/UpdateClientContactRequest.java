package coovitelCobranza.cobranzas.cliente.application.dto;

/**
 * Solicitud para actualizar la información de contacto de un cliente.
 * Permite modificar el teléfono y correo electrónico del cliente.
 */
public record UpdateClientContactRequest(Long clientId, String phone, String email) {
}

