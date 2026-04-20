package coovitelCobranza.cobranzas.client.application.dto;

import coovitelCobranza.cobranzas.client.domain.model.Client;

import java.time.LocalDateTime;

/**
 * Respuesta de datos del cliente para operaciones de lectura.
 * Contiene la información completa del cliente incluyendo consentimientos
 * para canales de comunicación.
 */
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

    /**
     * Convierte un modelo de dominio Cliente a un DTO de respuesta.
     *
     * @param cliente modelo del cliente a convertir
     * @return respuesta del cliente mapeada desde el modelo de dominio
     */
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

