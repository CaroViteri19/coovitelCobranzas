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
 * Parser de archivos TXT de posición fija separados por pipe ({@code |})
 * para carga masiva de asociados.
 *
 * <p><b>Formato esperado del TXT:</b>
 * <ul>
 *   <li>Separador: pipe {@code |}</li>
 *   <li>Sin fila de encabezado</li>
 *   <li>Encoding: UTF-8 (con o sin BOM)</li>
 *   <li>15 columnas en el mismo orden que el CSV:
 *       TIPO_ID | NUM_DOCUMENTO | NOMBRE_COMPLETO | NUM_OBLIGACION |
 *       SALDO_TOTAL | DIAS_MORA | FECHA_VENC | TELEFONO_1 |
 *       EMAIL | TELEFONO_2 | CIUDAD | CANAL_PREFERIDO |
 *       SEGMENTO | PRODUCTO | CODIGO_AGENTE</li>
 *   <li>Campos opcionales vacíos representados como {@code ||} (dos pipes consecutivos)</li>
 * </ul>
 *
 * <p><b>Procesamiento en streaming:</b> Lee el archivo línea por línea con
 * {@link BufferedReader}, sin cargar todo el contenido en memoria.
 */
@Component
public class TxtAssociateParser {

    private static final Logger log = LoggerFactory.getLogger(TxtAssociateParser.class);

    private static final String SEPARADOR   = "\\|";
    private static final int    BUFFER_SIZE = 65_536; // 64 KB
    private static final int    MAX_COLUMNS = AssociateRowDTO.EXPECTED_COLUMNS;

    // ── API pública ───────────────────────────────────────────────────────────

    /**
     * Parsea el archivo TXT desde el InputStream y devuelve todos los registros.
     *
     * @param inputStream stream del archivo multipart
     * @return lista de DTOs con todos los campos raw (sin validar)
     * @throws IOException si hay un error de I/O leyendo el archivo
     */
    public List<AssociateRowDTO> parse(InputStream inputStream) throws IOException {
        List<AssociateRowDTO> rows = new ArrayList<>();

        InputStream safeStream = stripBom(new BufferedInputStream(inputStream, BUFFER_SIZE));

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(safeStream, StandardCharsets.UTF_8),
                BUFFER_SIZE)) {

            String line;
            int rowNumber = 1;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    log.debug("[TXT-PARSER] Fila {} ignorada (vacía)", rowNumber);
                    rowNumber++;
                    continue;
                }
                rows.add(parseLine(line, rowNumber));
                rowNumber++;
            }
        }

        log.debug("[TXT-PARSER] Parseadas {} filas", rows.size());
        return rows;
    }

    // ── Parse de una línea ────────────────────────────────────────────────────

    private AssociateRowDTO parseLine(String line, int rowNumber) {
        // Dividir por pipe con -1 para preservar campos vacíos al final
        String[] cols = line.split(SEPARADOR, -1);

        // Padding: si faltan columnas opcionales, completar con null
        String[] padded = new String[MAX_COLUMNS];
        for (int i = 0; i < MAX_COLUMNS; i++) {
            padded[i] = (i < cols.length) ? cols[i].trim() : null;
        }

        return new AssociateRowDTO(
                rowNumber,
                padded[0],                // TIPO_ID
                padded[1],                // NUM_DOCUMENTO
                padded[2],                // NOMBRE_COMPLETO
                padded[3],                // NUM_OBLIGACION
                padded[4],                // SALDO_TOTAL (raw)
                padded[5],                // DIAS_MORA (raw)
                padded[6],                // FECHA_VENC (raw)
                padded[7],                // TELEFONO_1
                emptyToNull(padded[8]),   // EMAIL
                emptyToNull(padded[9]),   // TELEFONO_2
                emptyToNull(padded[10]),  // CIUDAD
                emptyToNull(padded[11]),  // CANAL_PREFERIDO
                emptyToNull(padded[12]),  // SEGMENTO
                emptyToNull(padded[13]),  // PRODUCTO
                emptyToNull(padded[14])   // CODIGO_AGENTE
        );
    }

    // ── Utilidades ────────────────────────────────────────────────────────────

    /**
     * Elimina BOM UTF-8 si está presente al inicio del stream.
     */
    private InputStream stripBom(InputStream in) throws IOException {
        PushbackInputStream pb = new PushbackInputStream(in, 3);
        byte[] bom = new byte[3];
        int read = pb.read(bom, 0, 3);

        if (read == 3
                && (bom[0] & 0xFF) == 0xEF
                && (bom[1] & 0xFF) == 0xBB
                && (bom[2] & 0xFF) == 0xBF) {
            log.info("[TXT-PARSER] BOM UTF-8 detectado y eliminado");
            return pb;
        }

        if (read > 0) {
            pb.unread(bom, 0, read);
        }
        return pb;
    }

    private String emptyToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
