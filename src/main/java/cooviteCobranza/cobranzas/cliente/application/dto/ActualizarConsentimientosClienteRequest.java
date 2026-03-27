package cooviteCobranza.cobranzas.cliente.application.dto;

public record ActualizarConsentimientosClienteRequest(
        boolean aceptaWhatsApp,
        boolean aceptaSms,
        boolean aceptaEmail
) {
}

