package coovitelCobranza.cobranzas.cargamasiva.application.service;

import coovitelCobranza.cobranzas.cargamasiva.application.dto.AsociadoRowDTO;
import coovitelCobranza.cobranzas.cargamasiva.application.dto.CargaMasivaResultResponse;
import coovitelCobranza.cobranzas.cargamasiva.application.dto.RowErrorDTO;
import coovitelCobranza.cobranzas.cargamasiva.application.exception.CargaMasivaException;
import coovitelCobranza.cobranzas.cargamasiva.domain.model.AsociadoImport;
import coovitelCobranza.cobranzas.cargamasiva.domain.repository.AsociadoImportRepository;
import coovitelCobranza.cobranzas.cargamasiva.domain.service.AsociadoImportValidationService;
import coovitelCobranza.cobranzas.cargamasiva.infrastructure.parser.CsvAsociadoParser;
import coovitelCobranza.cobranzas.auditoria.domain.service.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio de aplicación orquestador del proceso de carga masiva de asociados.
 *
 * <p><b>Pipeline de procesamiento:</b>
 * <ol>
 *   <li>Parse streaming del CSV</li>
 *   <li>Validación estructural por fila (domain service)</li>
 *   <li>Validación de unicidad contra BD</li>
 *   <li>Si hay errores → lanzar {@link CargaMasivaException} (rollback garantizado)</li>
 *   <li>Si limpio → batch insert via {@link AsociadoImportRepository#batchInsert}</li>
 *   <li>Registro de auditoría</li>
 * </ol>
 *
 * <p><b>Escalabilidad futura:</b> Reemplazar la llamada síncrona por publicación de
 * evento en Kafka/RabbitMQ y delegar el procesamiento a un consumer independiente.
 */
@Service
public class CargaMasivaApplicationService {

    private static final Logger log = LoggerFactory.getLogger(CargaMasivaApplicationService.class);

    private static final int BATCH_CHUNK_SIZE    = 500;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.BASIC_ISO_DATE; // YYYYMMDD

    private final CsvAsociadoParser             csvParser;
    private final AsociadoImportValidationService validationService;
    private final AsociadoImportRepository       repository;
    private final AuditService                   auditService;

    public CargaMasivaApplicationService(CsvAsociadoParser csvParser,
                                          AsociadoImportValidationService validationService,
                                          AsociadoImportRepository repository,
                                          AuditService auditService) {
        this.csvParser         = csvParser;
        this.validationService = validationService;
        this.repository        = repository;
        this.auditService      = auditService;
    }

    // ── API pública ───────────────────────────────────────────────────────────

    /**
     * Procesa un archivo CSV de carga masiva de forma síncrona y transaccional.
     *
     * <p>Si cualquier validación falla, la transacción se revierte automáticamente
     * (gracias a {@code @Transactional}) y se lanza {@link CargaMasivaException}.
     *
     * @param file       archivo CSV recibido del frontend
     * @param uploadedBy usuario que ejecuta la carga (para auditoría)
     * @return resultado de la carga con estadísticas
     */
    @Transactional(rollbackFor = Exception.class)
    public CargaMasivaResultResponse procesarCarga(MultipartFile file, String uploadedBy) {

        String fileName = sanitizeFileName(file.getOriginalFilename());
        log.info("[CARGA-MASIVA] Iniciando procesamiento: archivo='{}', usuario='{}', tamaño={}B",
                fileName, uploadedBy, file.getSize());

        // ── PASO 1: Parse streaming del CSV ───────────────────────────────────
        List<AsociadoRowDTO> rows;
        try {
            rows = csvParser.parse(file.getInputStream());
        } catch (IOException e) {
            log.error("[CARGA-MASIVA] Error leyendo el archivo '{}': {}", fileName, e.getMessage());
            throw new CargaMasivaException(
                    "No se pudo leer el archivo: " + e.getMessage(), fileName);
        } catch (IllegalArgumentException e) {
            throw new CargaMasivaException(e.getMessage(), fileName);
        }

        log.info("[CARGA-MASIVA] Parseadas {} filas de '{}'", rows.size(), fileName);

        // ── PASO 2: Validación estructural (domain service) ───────────────────
        List<RowErrorDTO> errors = new ArrayList<>(validationService.validateAll(rows));

        // ── PASO 3: Validación de unicidad contra BD ──────────────────────────
        if (errors.isEmpty()) {
            errors.addAll(validateDbUniqueness(rows));
        }

        // ── PASO 4: Abortar si hay errores ────────────────────────────────────
        if (!errors.isEmpty()) {
            log.warn("[CARGA-MASIVA] Validación fallida: {} errores en '{}' ({} filas)",
                    errors.size(), fileName, rows.size());
            auditService.registerEvent("INTEGRATION", "CARGA_MASIVA", null,
                    "UPLOAD_FAILED", uploadedBy, "SISTEMA", "WEB",
                    "Archivo '%s' rechazado: %d errores".formatted(fileName, errors.size()), null);
            throw new CargaMasivaException(errors, rows.size(), fileName);
        }

        // ── PASO 5: Convertir DTOs → Domain models ────────────────────────────
        List<AsociadoImport> asociados = rows.stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        // ── PASO 6: Batch insert ──────────────────────────────────────────────
        log.info("[CARGA-MASIVA] Insertando {} registros en chunks de {}...",
                asociados.size(), BATCH_CHUNK_SIZE);
        repository.batchInsert(asociados, BATCH_CHUNK_SIZE);

        // ── PASO 7: Auditoría ─────────────────────────────────────────────────
        auditService.registerEvent("INTEGRATION", "CARGA_MASIVA", null,
                "UPLOAD_SUCCESS", uploadedBy, "SISTEMA", "WEB",
                "Archivo '%s' procesado: %d registros insertados".formatted(fileName, rows.size()),
                null);

        log.info("[CARGA-MASIVA] Procesamiento exitoso: {} registros en '{}'",
                rows.size(), fileName);

        return CargaMasivaResultResponse.ok(rows.size(), fileName);
    }

    // ── Validación de unicidad contra BD ─────────────────────────────────────

    private List<RowErrorDTO> validateDbUniqueness(List<AsociadoRowDTO> rows) {
        List<RowErrorDTO> errors = new ArrayList<>();

        // Documentos en el archivo
        Set<String> docsEnArchivo = rows.stream()
                .filter(r -> r.numDocumento() != null && !r.numDocumento().isBlank())
                .map(r -> r.numDocumento().trim().toUpperCase())
                .collect(Collectors.toSet());

        Set<String> docsExistentes = repository.findExistingDocumentos(docsEnArchivo);
        if (!docsExistentes.isEmpty()) {
            rows.stream()
                .filter(r -> r.numDocumento() != null &&
                             docsExistentes.contains(r.numDocumento().trim().toUpperCase()))
                .forEach(r -> errors.add(RowErrorDTO.field(r.rowNumber(), "NUM_DOCUMENTO",
                        "Documento '%s' ya existe en la base de datos.".formatted(r.numDocumento()))));
        }

        // Emails en el archivo (solo los no vacíos)
        Set<String> emailsEnArchivo = rows.stream()
                .filter(r -> r.email() != null && !r.email().isBlank())
                .map(r -> r.email().trim().toLowerCase())
                .collect(Collectors.toSet());

        if (!emailsEnArchivo.isEmpty()) {
            Set<String> emailsExistentes = repository.findExistingEmails(emailsEnArchivo);
            if (!emailsExistentes.isEmpty()) {
                rows.stream()
                    .filter(r -> r.email() != null &&
                                 emailsExistentes.contains(r.email().trim().toLowerCase()))
                    .forEach(r -> errors.add(RowErrorDTO.field(r.rowNumber(), "EMAIL",
                            "Email '%s' ya existe en la base de datos.".formatted(r.email()))));
            }
        }

        return errors;
    }

    // ── Conversión DTO → Domain model ─────────────────────────────────────────

    private AsociadoImport toModel(AsociadoRowDTO row) {
        return AsociadoImport.create(
                row.tipoId().trim().toUpperCase(),
                row.numDocumento().trim().toUpperCase(),
                row.nombreCompleto().trim(),
                row.numObligacion().trim(),
                new BigDecimal(row.saldoTotalRaw().replace(",", ".")),
                Integer.parseInt(row.diasMoraRaw().trim()),
                LocalDate.parse(row.fechaVencRaw().trim(), DATE_FMT),
                row.telefono1().trim(),
                nullableField(row.email()),
                nullableField(row.telefono2()),
                nullableField(row.ciudad()),
                nullableField(row.canalPreferido()),
                nullableField(row.segmento()),
                nullableField(row.producto()),
                nullableField(row.codigoAgente())
        );
    }

    private String nullableField(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    private String sanitizeFileName(String fileName) {
        if (fileName == null) return "unknown.csv";
        return fileName.replaceAll("[^a-zA-Z0-9._\\-]", "_");
    }
}
