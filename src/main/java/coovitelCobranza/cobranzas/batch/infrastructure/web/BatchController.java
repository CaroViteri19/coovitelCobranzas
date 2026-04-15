package coovitelCobranza.cobranzas.batch.infrastructure.web;

import coovitelCobranza.cobranzas.batch.application.dto.BatchResultResponse;
import coovitelCobranza.cobranzas.batch.application.service.BatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Endpoint para carga batch de clientes y obligaciones.
 *
 * POST /api/carga-batch
 * Content-Type: multipart/form-data
 * Parámetro: file (archivo .csv o .txt)
 */
@RestController
@RequestMapping("/api/carga-batch")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Carga Batch", description = "Ingesta masiva de clientes y obligaciones vía CSV o TXT")
public class BatchController {

    private static final Logger log = LoggerFactory.getLogger(BatchController.class);

    private final BatchService batchService;

    public BatchController(BatchService batchService) {
        this.batchService = batchService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Cargar archivo batch",
        description = "Carga un archivo CSV (separador coma, con encabezados) o TXT (separador pipe, sin encabezados) "
                    + "para insertar o actualizar clientes y obligaciones.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Proceso ejecutado (puede tener errores parciales)",
                content = @Content(schema = @Schema(implementation = BatchResultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Archivo inválido o vacío"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        }
    )
    public ResponseEntity<BatchResultResponse> cargarArchivo(
            @Parameter(description = "Archivo .csv o .txt a procesar", required = true)
            @RequestParam("file") MultipartFile file) throws Exception {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío o no fue proporcionado.");
        }

        log.info("Solicitud de carga batch recibida. Archivo: {}, Tamaño: {} bytes",
                file.getOriginalFilename(), file.getSize());

        BatchResultResponse resultado = batchService.procesarArchivo(file);
        return ResponseEntity.ok(resultado);
    }
}
