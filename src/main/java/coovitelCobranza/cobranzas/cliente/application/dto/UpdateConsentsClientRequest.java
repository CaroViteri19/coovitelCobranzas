package coovitelCobranza.cobranzas.cliente.application.dto;

public record UpdateConsentsClientRequest(
        boolean aceptaWhatsApp,
        boolean aceptaSms,
        boolean aceptaEmail
) {
}

