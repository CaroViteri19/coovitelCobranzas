package coovitelCobranza.cobranzas.bulkimport.infrastructure.parser;

import coovitelCobranza.cobranzas.bulkimport.application.dto.AssociateRowDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser de archivos CSV para carga masiva de asociados.
 *
 * <p><b>Procesamiento en streaming:</b> Lee el archivo línea por línea usando
 * {@link BufferedReader}, sin cargar todo el contenido en memoria. Optimizado
 * para archivos de ~10,000 registros con buffer de 64KB.
 *
 * <p><b>Formato esperado del CSV:</b>
 * <ul>
 *   <li>Separador: coma (,)</li>
 *   <li>Primera fila: encabezado (ignorado, solo validado)</li>
 *   <li>Encoding: UTF-8 (con o sin BOM)</li>
 *   <li>Columnas opcionales: pueden estar vacías pero deben estar presentes como columnas</li>
 * </ul>
 *
 * <p><b>Evolución futura:</b> Puede extenderse para devolver un {@code Stream<AssociateRowDTO>}
 * lazy cuando se integre con Spring Batch (ItemReader pattern).
 */
@Component
public class CsvAssociateParser {

    private static final Logger log = LoggerFactory.getLogger(CsvAssociateParser.class);

    private static final char SEPARATOR      = ',';
    private static final int  BUFFER_SIZE    = 65_536; // 64KB
    private static final int  MIN_COLUMNS    = 8;      // Hasta TELEFONO_1 (obligatorios)
    private static final int  MAX_COLUMNS    = AssociateRowDTO.EXPECTED_COLUMNS;

    // ── API pública ───────────────────────────────────────────────────────────

    /**
     * Parsea el archivo CSV desde el InputStream y devuelve todos los registros.
     *
     * @param inputStream stream del archivo multipart
     * @return lista de DTOs con todos los campos raw (sin validar)
     * @throws IOException            si hay un error de I/O leyendo el archivo
     * @throws IllegalArgumentException si el encabezado no tiene la estructura esperada
     */
    public List<AssociateRowDTO> parse(InputStream inputStream) throws IOException {
        List<AssociateRowDTO> rows = new ArrayList<>();

        // Envuelve en BufferedInputStream antes de stripBom para garantizar
        // que el stream soporte operaciones de lectura robusta (FileInputStream no soporta mark/reset)
        InputStream safeStream = stripBom(new BufferedInputStream(inputStream, BUFFER_SIZE));

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(safeStream, StandardCharsets.UTF_8),
                BUFFER_SIZE)) {

            // ── Leer y validar encabezado ──────────────────────────────────────
            String headerLine = reader.readLine();
            if (headerLine == null || headerLine.isBlank()) {
                throw new IllegalArgumentException(
                        "El archivo CSV está vacío o no tiene encabezado.");
            }
            validateHeader(headerLine);

            // ── Leer filas de datos (streaming) ───────────────────────────────
            String line;
            int rowNumber = 1;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    log.debug("Fila {} ignorada (vacía)", rowNumber + 1);
                    rowNumber++;
                    continue;
                }
                rows.add(parseLine(line, rowNumber));
                rowNumber++;
            }
        }

        log.debug("[CSV-PARSER] Parseadas {} filas", rows.size());
        return rows;
    }

    // ── Parse de una línea ────────────────────────────────────────────────────

    private AssociateRowDTO parseLine(String line, int rowNumber) {
        String[] cols = splitCsvLine(line);

        // Padding: si faltan columnas opcionales, completar con null
        String[] padded = new String[MAX_COLUMNS];
        for (int i = 0; i < MAX_COLUMNS; i++) {
            padded[i] = (i < cols.length) ? cols[i].trim() : null;
        }

        return new AssociateRowDTO(
                rowNumber,
                padded[0],   // TIPO_ID
                padded[1],   // NUM_DOCUMENTO
                padded[2],   // NOMBRE_COMPLETO
                padded[3],   // NUM_OBLIGACION
                padded[4],   // SALDO_TOTAL (raw)
                padded[5],   // DIAS_MORA (raw)
                padded[6],   // FECHA_VENC (raw)
                padded[7],   // TELEFONO_1
                emptyToNull(padded[8]),   // EMAIL
                emptyToNull(padded[9]),   // TELEFONO_2
                emptyToNull(padded[10]),  // CIUDAD
                emptyToNull(padded[11]),  // CANAL_PREFERIDO
                emptyToNull(padded[12]),  // SEGMENTO
                emptyToNull(padded[13]),  // PRODUCTO
                emptyToNull(padded[14])   // CODIGO_AGENTE
        );
    }

    // ── Split respetando comillas (RFC 4180 básico) ───────────────────────────

    /**
     * Divide una línea CSV respetando valores entre comillas dobles.
     * Soporta comas dentro de valores entrecomillados: {@code "nombre, apellido"}.
     */
    private String[] splitCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // Escaped quote dentro de campo entrecomillado
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == SEPARATOR && !inQuotes) {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString()); // Último campo

        return fields.toArray(new String[0]);
    }

    // ── Validación de encabezado ──────────────────────────────────────────────

    /**
     * Valida que el encabezado tenga al menos las columnas obligatorias en el orden correcto.
     */
    private void validateHeader(String headerLine) {
        String[] headers = splitCsvLine(headerLine.trim());

        if (headers.length < MIN_COLUMNS) {
            throw new IllegalArgumentException(
                    "El encabezado tiene %d columnas. Se requieren al menos %d: %s"
                    .formatted(headers.length, MIN_COLUMNS,
                               String.join(", ", AssociateRowDTO.COLUMN_NAMES)));
        }

        // Validar las 8 columnas obligatorias por nombre (case-insensitive)
        for (int i = 0; i < MIN_COLUMNS; i++) {
            String expected = AssociateRowDTO.COLUMN_NAMES[i];
            String actual   = headers[i].trim().replaceAll("[\"']", "").toUpperCase();
            if (!expected.equalsIgnoreCase(actual)) {
                throw new IllegalArgumentException(
                        "Columna %d del encabezado inválida. Esperado: '%s', encontrado: '%s'."
                        .formatted(i + 1, expected, actual));
            }
        }
    }

    // ── Utilidades ────────────────────────────────────────────────────────────

    /**
     * Elimina BOM UTF-8 si está presente al inicio del stream.
     *
     * <p>Usa {@link PushbackInputStream} en vez de mark/reset para funcionar con
     * cualquier tipo de InputStream (incluyendo {@code FileInputStream} que NO soporta mark).
     */
    private InputStream stripBom(InputStream in) throws IOException {
        PushbackInputStream pb = new PushbackInputStream(in, 3);
        byte[] bom = new byte[3];
        int read = pb.read(bom, 0, 3);

        if (read == 3
                && (bom[0] & 0xFF) == 0xEF
                && (bom[1] & 0xFF) == 0xBB
                && (bom[2] & 0xFF) == 0xBF) {
            log.info("[CSV-PARSER] BOM UTF-8 detectado y eliminado");
            return pb; // BOM consumido; el resto del stream sigue intacto
        }

        // No hay BOM: devolver los bytes leídos al stream
        if (read > 0) {
            pb.unread(bom, 0, read);
        }
        return pb;
    }

    private String emptyToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
