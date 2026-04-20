package coovitelCobranza.cobranzas.cargamasiva.application.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Respuesta unificada del proceso de carga masiva.
 *
 * <p><b>En éxito:</b> {@code success=true}, {@code errors} vacío, {@code totalInserted > 0}.
 * <br><b>En fallo:</b> {@code success=false}, {@code errors} con detalle por fila/campo.
 *
 * <p>Ejemplo de respuesta exitosa:
 * <pre>{@code
 * {
 *   "success": true,
 *   "totalRows": 10000,
 *   "totalInserted": 10000,
 *   "totalErrors": 0,
 *   "fileName": "cartera_marzo.csv",
 *   "processedAt": "2024-03-15T10:30:00",
 *   "errors": []
 * }
 * }</pre>
 *
 * <p>Ejemplo de respuesta con errores:
 * <pre>{@code
 * {
 *   "success": false,
 *   "totalRows": 10000,
 *   "totalInserted": 0,
 *   "totalErrors": 3,
 *   "fileName": "cartera_marzo.csv",
 *   "processedAt": "2024-03-15T10:30:00",
 *   "errors": [
 *     { "rowNumber": 42,  "field": "EMAIL",        "message": "Formato de email inválido: 'jdoe@'", "severity": "ERROR" },
 *     { "rowNumber": 87,  "field": "SALDO_TOTAL",  "message": "Formato numérico inválido: 'N/A'",   "severity": "ERROR" },
 *     { "rowNumber": 156, "field": "NUM_DOCUMENTO", "message": "Documento '12345678' duplicado",    "severity": "ERROR" }
 *   ]
 * }
 * }</pre>
 */
public record CargaMasivaResultResponse(
        boolean           success,
        int               totalRows,
        int               totalInserted,
        int               totalErrors,
        String            fileName,
        LocalDateTime     processedAt,
        List<RowErrorDTO> errors
) {
    /** Factory: resultado exitoso. */
    public static CargaMasivaResultResponse ok(int totalRows, String fileName) {
        return new CargaMasivaResultResponse(
                true, totalRows, totalRows, 0, fileName, LocalDateTime.now(), List.of()
        );
    }

    /** Factory: resultado fallido con lista de errores. */
    public static CargaMasivaResultResponse failed(int totalRows,
                                                    String fileName,
                                                    List<RowErrorDTO> errors) {
        return new CargaMasivaResultResponse(
                false, totalRows, 0, errors.size(), fileName, LocalDateTime.now(), errors
        );
    }
}
