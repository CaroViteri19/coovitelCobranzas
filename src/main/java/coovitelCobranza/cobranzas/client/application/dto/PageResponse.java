package coovitelCobranza.cobranzas.client.application.dto;

import java.util.List;

/**
 * Contenedor genérico para respuestas paginadas.
 *
 * @param <T> tipo del contenido paginado
 * @param content       registros de la página actual
 * @param page          índice de página (0-based)
 * @param size          tamaño de página solicitado
 * @param totalElements total de registros en la tabla
 * @param totalPages    cantidad total de páginas
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
