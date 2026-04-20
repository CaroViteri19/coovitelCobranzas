package coovitelCobranza.cobranzas.cargamasiva.application.dto;

/**
 * DTO que representa una fila parseada del CSV antes de ser validada.
 *
 * <p>Todos los campos numéricos/fecha se mantienen como String ({@code *Raw})
 * para permitir validación granular de formato antes de la conversión de tipos.
 *
 * @param rowNumber      Número de fila en el CSV (base 1, sin contar el header)
 * @param tipoId         TIPO_ID raw
 * @param numDocumento   NUM_DOCUMENTO raw
 * @param nombreCompleto NOMBRE_COMPLETO raw
 * @param numObligacion  NUM_OBLIGACION raw
 * @param saldoTotalRaw  SALDO_TOTAL como String (conversión en validación)
 * @param diasMoraRaw    DIAS_MORA como String (conversión en validación)
 * @param fechaVencRaw   FECHA_VENC como String YYYYMMDD (conversión en validación)
 * @param telefono1      TELEFONO_1
 * @param email          EMAIL (nullable)
 * @param telefono2      TELEFONO_2 (nullable)
 * @param ciudad         CIUDAD (nullable)
 * @param canalPreferido CANAL_PREFERIDO (nullable)
 * @param segmento       SEGMENTO (nullable)
 * @param producto       PRODUCTO (nullable)
 * @param codigoAgente   CODIGO_AGENTE (nullable)
 */
public record AsociadoRowDTO(
        int    rowNumber,
        String tipoId,
        String numDocumento,
        String nombreCompleto,
        String numObligacion,
        String saldoTotalRaw,
        String diasMoraRaw,
        String fechaVencRaw,
        String telefono1,
        String email,
        String telefono2,
        String ciudad,
        String canalPreferido,
        String segmento,
        String producto,
        String codigoAgente
) {
    /** Número de columnas esperadas en el CSV (incluyendo opcionales hasta CODIGO_AGENTE). */
    public static final int EXPECTED_COLUMNS = 15;

    /** Nombres de columna en orden para mensajes de error claros. */
    public static final String[] COLUMN_NAMES = {
            "TIPO_ID", "NUM_DOCUMENTO", "NOMBRE_COMPLETO", "NUM_OBLIGACION",
            "SALDO_TOTAL", "DIAS_MORA", "FECHA_VENC", "TELEFONO_1",
            "EMAIL", "TELEFONO_2", "CIUDAD", "CANAL_PREFERIDO",
            "SEGMENTO", "PRODUCTO", "CODIGO_AGENTE"
    };
}
