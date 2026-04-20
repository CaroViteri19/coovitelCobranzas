package coovitelCobranza.cobranzas.batch.application.dto;

/**
 * Detalle de error por fila en la carga batch.
 */
public record BatchErrorDetail(int fila, String mensaje) {}
