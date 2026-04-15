package coovitelCobranza.cobranzas.batch.infrastructure.parser;

import coovitelCobranza.cobranzas.batch.application.dto.CargaBatchDTO;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase base con utilidades compartidas entre los parsers CSV y TXT.
 * Define el contrato de parsing y la lógica de validación/conversión de campos.
 */
public abstract class BatchFileParser {

    protected static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    // ── Contrato ──────────────────────────────────────────────────────────────

    /**
     * Lee el InputStream y retorna la lista de filas parseadas.
     * Cada entrada de la lista corresponde a una fila del archivo;
     * las filas con error se reportan como null con el mensaje almacenado
     * en {@code erroresParseo}.
     *
     * @param inputStream  stream del archivo cargado
     * @param erroresParseo lista donde se acumulan los mensajes de error de parseo
     * @return filas válidas parseadas
     */
    public abstract List<CargaBatchDTO> parse(InputStream inputStream,
                                              List<String> erroresParseo) throws Exception;

    // ── Utilidades de validación y conversión ─────────────────────────────────

    protected CargaBatchDTO mapearColumnas(String[] cols, int fila, List<String> errores) {
        // Normaliza: trim y convierte vacíos a null
        String[] c = new String[15];
        for (int i = 0; i < 15; i++) {
            c[i] = (i < cols.length && cols[i] != null && !cols[i].isBlank())
                    ? cols[i].trim() : null;
        }

        List<String> erroresFila = new ArrayList<>();

        // ── Campos requeridos ───────────────────────────────────────────────
        String tipoId          = validarRequerido(c[0],  "TIPO_ID",          fila, erroresFila);
        String numDocumento    = validarRequerido(c[1],  "NUM_DOCUMENTO",     fila, erroresFila);
        String nombreCompleto  = validarRequerido(c[2],  "NOMBRE_COMPLETO",   fila, erroresFila);
        String numObligacion   = validarRequerido(c[3],  "NUM_OBLIGACION",    fila, erroresFila);
        BigDecimal saldoTotal  = parsearDecimal(c[4],    "SALDO_TOTAL",       fila, erroresFila);
        Integer diasMora       = parsearEntero(c[5],     "DIAS_MORA",         fila, erroresFila);
        LocalDate fechaVenc    = parsearFecha(c[6],      "FECHA_VENC",        fila, erroresFila);
        String telefono1       = validarRequerido(c[7],  "TELEFONO_1",        fila, erroresFila);

        // Validar longitudes
        validarLongitud(tipoId,         "TIPO_ID",          2,  fila, erroresFila);
        validarLongitud(numDocumento,   "NUM_DOCUMENTO",    20, fila, erroresFila);
        validarLongitud(nombreCompleto, "NOMBRE_COMPLETO", 120, fila, erroresFila);
        validarLongitud(numObligacion,  "NUM_OBLIGACION",   30, fila, erroresFila);
        validarLongitud(telefono1,      "TELEFONO_1",       15, fila, erroresFila);

        // ── Campos opcionales ───────────────────────────────────────────────
        String email         = validarLongitudOpcional(c[8],  "EMAIL",          80, fila, erroresFila);
        String telefono2     = validarLongitudOpcional(c[9],  "TELEFONO_2",     15, fila, erroresFila);
        String ciudad        = validarLongitudOpcional(c[10], "CIUDAD",         60, fila, erroresFila);
        String canalPreferido= validarLongitudOpcional(c[11], "CANAL_PREFERIDO",20, fila, erroresFila);
        String segmento      = validarLongitudOpcional(c[12], "SEGMENTO",       30, fila, erroresFila);
        String producto      = validarLongitudOpcional(c[13], "PRODUCTO",       50, fila, erroresFila);
        String codigoAgente  = validarLongitudOpcional(c[14], "CODIGO_AGENTE",  10, fila, erroresFila);

        if (!erroresFila.isEmpty()) {
            errores.addAll(erroresFila);
            return null;
        }

        CargaBatchDTO dto = new CargaBatchDTO();
        dto.setTipoId(tipoId);
        dto.setNumDocumento(numDocumento);
        dto.setNombreCompleto(nombreCompleto);
        dto.setNumObligacion(numObligacion);
        dto.setSaldoTotal(saldoTotal);
        dto.setDiasMora(diasMora);
        dto.setFechaVenc(fechaVenc);
        dto.setTelefono1(telefono1);
        dto.setEmail(email);
        dto.setTelefono2(telefono2);
        dto.setCiudad(ciudad);
        dto.setCanalPreferido(canalPreferido);
        dto.setSegmento(segmento);
        dto.setProducto(producto);
        dto.setCodigoAgente(codigoAgente);
        return dto;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String validarRequerido(String valor, String campo, int fila, List<String> errores) {
        if (valor == null) {
            errores.add("Fila " + fila + ": " + campo + " es requerido.");
            return null;
        }
        return valor;
    }

    private void validarLongitud(String valor, String campo, int max, int fila, List<String> errores) {
        if (valor != null && valor.length() > max) {
            errores.add("Fila " + fila + ": " + campo + " supera " + max + " caracteres.");
        }
    }

    private String validarLongitudOpcional(String valor, String campo, int max, int fila, List<String> errores) {
        if (valor != null && valor.length() > max) {
            errores.add("Fila " + fila + ": " + campo + " supera " + max + " caracteres.");
        }
        return valor;
    }

    private BigDecimal parsearDecimal(String valor, String campo, int fila, List<String> errores) {
        if (valor == null) {
            errores.add("Fila " + fila + ": " + campo + " es requerido.");
            return null;
        }
        try {
            return new BigDecimal(valor.replace(",", "."));
        } catch (NumberFormatException e) {
            errores.add("Fila " + fila + ": " + campo + " debe ser decimal (ej: 1234.56).");
            return null;
        }
    }

    private Integer parsearEntero(String valor, String campo, int fila, List<String> errores) {
        if (valor == null) {
            errores.add("Fila " + fila + ": " + campo + " es requerido.");
            return null;
        }
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            errores.add("Fila " + fila + ": " + campo + " debe ser entero.");
            return null;
        }
    }

    private LocalDate parsearFecha(String valor, String campo, int fila, List<String> errores) {
        if (valor == null) {
            errores.add("Fila " + fila + ": " + campo + " es requerido.");
            return null;
        }
        try {
            return LocalDate.parse(valor, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            errores.add("Fila " + fila + ": " + campo + " debe tener formato YYYYMMDD (ej: 20241231).");
            return null;
        }
    }
}
