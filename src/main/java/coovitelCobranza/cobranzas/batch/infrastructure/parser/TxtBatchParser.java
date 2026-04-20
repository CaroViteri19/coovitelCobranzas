package coovitelCobranza.cobranzas.batch.infrastructure.parser;

import coovitelCobranza.cobranzas.batch.application.dto.CargaBatchDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser para archivos TXT:
 * - Separador: pipe (|)
 * - Sin encabezados
 * - Codificación: UTF-8
 * - Procesamiento por streaming (BufferedReader línea a línea)
 */
@Component
public class TxtBatchParser extends BatchFileParser {

    private static final Logger log = LoggerFactory.getLogger(TxtBatchParser.class);
    private static final String SEPARADOR = "\\|";

    @Override
    public List<CargaBatchDTO> parse(InputStream inputStream, List<String> erroresParseo) throws Exception {
        List<CargaBatchDTO> resultado = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String linea;
            int numeroFila = 1;

            while ((linea = reader.readLine()) != null) {
                if (linea.isBlank()) {
                    numeroFila++;
                    continue;
                }

                String[] columnas = linea.split(SEPARADOR, -1); // -1 preserva campos vacíos al final
                log.debug("Procesando fila TXT {}: {} columnas", numeroFila, columnas.length);

                List<String> erroresFila = new ArrayList<>();
                CargaBatchDTO dto = mapearColumnas(columnas, numeroFila, erroresFila);

                if (dto != null) {
                    resultado.add(dto);
                } else {
                    erroresParseo.addAll(erroresFila);
                }
                numeroFila++;
            }
        }

        log.info("TXT parseado: {} filas válidas, {} filas con error.",
                resultado.size(), erroresParseo.size());
        return resultado;
    }
}
