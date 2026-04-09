package coovitelCobranza.cobranzas.cliente.application.dto;

/**
 * Solicitud para buscar un cliente por tipo y número de documento.
 * Utiliza los datos documentales como criterio de búsqueda.
 */
public record GetClientByDocumentRequest(String documentType, String documentNumber) {
}

