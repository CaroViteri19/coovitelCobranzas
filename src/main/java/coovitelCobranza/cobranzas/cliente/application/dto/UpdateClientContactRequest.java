package coovitelCobranza.cobranzas.cliente.application.dto;

public record UpdateClientContactRequest(Long clientId, String phone, String email) {
}

