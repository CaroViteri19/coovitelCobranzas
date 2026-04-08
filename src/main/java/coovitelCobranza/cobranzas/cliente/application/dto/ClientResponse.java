package coovitelCobranza.cobranzas.cliente.application.dto;

import coovitelCobranza.cobranzas.cliente.domain.model.Client;

import java.time.LocalDateTime;

public record ClientResponse(
        Long id,
        String tipoDocumento,
        String numeroDocumento,
        String fullName,
        String telefono,
        String email,
        boolean aceptaWhatsApp,
        boolean aceptaSms,
        boolean aceptaEmail,
        LocalDateTime updatedAt
) {

    public static ClientResponse fromDomain(Client cliente) {
        return new ClientResponse(
                cliente.getId(),
                cliente.getTipoDocumento(),
                cliente.getNumeroDocumento(),
                cliente.getFullName(),
                cliente.getTelefono(),
                cliente.getEmail(),
                cliente.isAceptaWhatsApp(),
                cliente.isAceptaSms(),
                cliente.isAceptaEmail(),
                cliente.getUpdatedAt()
        );
    }
}

