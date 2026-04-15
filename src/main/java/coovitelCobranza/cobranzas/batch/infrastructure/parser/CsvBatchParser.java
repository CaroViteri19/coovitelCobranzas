package coovitelCobranza.cobranzas.batch.infrastructure.parser;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.RFC4180Parser;
import com.opencsv.RFC4180ParserBuilder;
import coovitelCobranza.cobranzas.batch.application.dto.CargaBatchDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser para archivos CSV:
 * - Separador: coma (,)
 * - Primera fila: encabezados (se omite)
 * - Codificación: UTF-8
 * - Procesamiento por streaming (no carga todo en memoria)
 */
@Component
public class CsvBatchParser extends BatchFileParser {

    private static final Logger log = LoggerFactory.getLogger(CsvBatchParser.class);

    @Override
    public List<CargaBatchDTO> parse(InputStream inputStream, List<String> erroresParseo) throws Exception {
        List<CargaBatchDTO> resultado = new ArrayList<>();

        RFC4180Parser rfcParser = new RFC4180ParserBuilder().build();

        try (CSVReader reader = new CSVReaderBuilder(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .withCSVParser(rfcParser)
                .withSkipLines(1)   // omitir encabezados
                .build()) {

            String[] linea;
            int numeroFila = 2; // comienza en 2 porque la fila 1 es el encabezado

            while ((linea = reader.readNext()) != null) {
                log.debug("Procesando fila CSV {}: {} columnas", numeroFila, linea.length);

                List<String> erroresFila = new ArrayList<>();
                CargaBatchDTO dto = mapearColumnas(linea, numeroFila, erroresFila);

                if (dto != null) {
                    resultado.add(dto);
                } else {
                    erroresParseo.addAll(erroresFila);
                }
                numeroFila++;
            }
        }

        log.info("CSV parseado: {} filas válidas, {} filas con error.",
                resultado.size(), erroresParseo.size());
        return resultado;
    }
}
