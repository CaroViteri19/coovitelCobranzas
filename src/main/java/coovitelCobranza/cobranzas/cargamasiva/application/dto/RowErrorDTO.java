package coovitelCobranza.cobranzas.cargamasiva.application.dto;

/**
 * DTO que describe un error de validación en una fila específica del CSV.
 *
 * <p>Enviado al frontend dentro de {@link CargaMasivaResultResponse}.
 *
 * @param rowNumber Número de fila (1-based). {@code -1} para errores globales del archivo.
 * @param field     Nombre del campo con error (e.g., "EMAIL"). {@code null} para errores de fila.
 * @param message   Descripción legible del error.
 * @param severity  Nivel de severidad: "ERROR" | "WARNING"
 */
public record RowErrorDTO(
        int    rowNumber,
        String field,
        String message,
        String severity
) {
    /** Crea un error de campo específico con severidad ERROR. */
    public static RowErrorDTO field(int rowNumber, String field, String message) {
        return new RowErrorDTO(rowNumber, field, message, "ERROR");
    }

    /** Crea un error de fila (sin campo específico) con severidad ERROR. */
    public static RowErrorDTO row(int rowNumber, String message) {
        return new RowErrorDTO(rowNumber, null, message, "ERROR");
    }

    /** Crea un error global del archivo con rowNumber = -1. */
    public static RowErrorDTO global(String message) {
        return new RowErrorDTO(-1, null, message, "ERROR");
    }

    /** Crea una advertencia de campo (no bloquea la carga). */
    public static RowErrorDTO warning(int rowNumber, String field, String message) {
        return new RowErrorDTO(rowNumber, field, message, "WARNING");
    }
}
