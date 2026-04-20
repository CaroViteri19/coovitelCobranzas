package coovitelCobranza.cobranzas.cliente.application.dto;

/**
 * Solicitud de listado paginado de clientes.
 *
 * @param page índice de página 0-based (por defecto 0)
 * @param size tamaño de página (por defecto 10, máximo 100)
 */
public record ListClientsRequest(
        Integer page,
        Integer size
) {
    public int safePage() {
        return page == null || page < 0 ? 0 : page;
    }

    public int safeSize() {
        if (size == null || size <= 0) return 10;
        return Math.min(size, 100);
    }
}
