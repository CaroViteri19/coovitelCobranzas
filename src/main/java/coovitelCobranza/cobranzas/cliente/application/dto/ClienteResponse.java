package coovitelCobranza.cobranzas.cliente.application.dto;

import coovitelCobranza.cobranzas.cliente.domain.model.Cliente;

import java.time.LocalDateTime;

public record ClienteResponse(
        Long id,
        String tipoDocumento,
        String numeroDocumento,
        String nombreCompleto,
        String telefono,
        String email,
        boolean aceptaWhatsApp,
        boolean aceptaSms,
        boolean aceptaEmail,
        LocalDateTime updatedAt
) {

    public static ClienteResponse fromDomain(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getTipoDocumento(),
                cliente.getNumeroDocumento(),
                cliente.getNombreCompleto(),
                cliente.getTelefono(),
                cliente.getEmail(),
                cliente.isAceptaWhatsApp(),
                cliente.isAceptaSms(),
                cliente.isAceptaEmail(),
                cliente.getUpdatedAt()
        );
    }
}

