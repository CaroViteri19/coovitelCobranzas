package coovitelCobranza.cobranzas.bulkimport.application.exception;

import coovitelCobranza.cobranzas.bulkimport.application.dto.RowErrorDTO;

import java.util.List;

/**
 * Excepción de negocio para el proceso de carga masiva.
 *
 * <p>Se lanza cuando la validación detecta errores en el archivo CSV.
 * Contiene la lista completa de errores para ser devuelta al cliente
 * en un solo response, evitando múltiples round-trips.
 *
 * <p>Al ser una {@link RuntimeException} marcada, Spring la captura en
 * {@link coovitelCobranza.cobranzas.bulkimport.infrastructure.web.exception.BulkImportExceptionHandler}
 * y produce un HTTP 422.
 */
public class BulkImportException extends RuntimeException {

    private final List<RowErrorDTO> errors;
    private final int totalRows;
    private final String fileName;

    /**
     * Crea la excepción con todos los errores de validación.
     *
     * @param errors    lista de errores por fila/campo
     * @param totalRows total de filas del archivo procesado
     * @param fileName  nombre del archivo que generó los errores
     */
    public BulkImportException(List<RowErrorDTO> errors, int totalRows, String fileName) {
        super("Carga masiva fallida: %d errores en '%s' (%d filas procesadas)"
                .formatted(errors.size(), fileName, totalRows));
        this.errors   = List.copyOf(errors);
        this.totalRows = totalRows;
        this.fileName  = fileName;
    }

    /**
     * Crea la excepción para un único error global (ej: archivo vacío, formato inválido).
     */
    public BulkImportException(String message, String fileName) {
        this(List.of(RowErrorDTO.global(message)), 0, fileName);
    }

    public List<RowErrorDTO> getErrors()   { return errors; }
    public int getTotalRows()              { return totalRows; }
    public String getFileName()            { return fileName; }
}
