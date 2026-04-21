package coovitelCobranza.cobranzas.bulkimport.application.service;

import coovitelCobranza.cobranzas.audit.domain.context.AuditContext;
import coovitelCobranza.cobranzas.audit.domain.service.AuditService;
import coovitelCobranza.cobranzas.bulkimport.application.dto.AssociateRowDTO;
import coovitelCobranza.cobranzas.bulkimport.application.dto.BulkImportResultResponse;
import coovitelCobranza.cobranzas.bulkimport.application.dto.RowErrorDTO;
import coovitelCobranza.cobranzas.bulkimport.application.exception.BulkImportException;
import coovitelCobranza.cobranzas.bulkimport.domain.service.AssociateImportValidationService;
import coovitelCobranza.cobranzas.bulkimport.infrastructure.parser.CsvAssociateParser;
import coovitelCobranza.cobranzas.bulkimport.infrastructure.parser.TxtAssociateParser;
import coovitelCobranza.cobranzas.client.domain.model.Client;
import coovitelCobranza.cobranzas.client.domain.repository.ClientRepository;
import coovitelCobranza.cobranzas.obligation.domain.model.Obligation;
import coovitelCobranza.cobranzas.obligation.domain.repository.ObligationRepository;
import coovitelCobranza.util.TextUtils;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio de aplicación orquestador del proceso de carga masiva de asociados.
 *
 * <p><b>Formatos soportados:</b>
 * <ul>
 *   <li>{@code .csv} — separador coma, primera fila encabezado, encoding UTF-8</li>
 *   <li>{@code .txt} — separador pipe {@code |}, sin encabezado, encoding UTF-8</li>
 * </ul>
 *
 * <p><b>Pipeline de procesamiento:</b>
 * <ol>
 *   <li>Auditoría: registrar inicio (en transacción independiente)</li>
 *   <li>Parse streaming del archivo (CSV o TXT según extensión)</li>
 *   <li>Validación estructural por fila — {@link AssociateImportValidationService}</li>
 *   <li>Si hay errores → auditoría de fallo (en transacción independiente) + excepción</li>
 *   <li>Por cada fila válida: upsert de {@link Client} + upsert de {@link Obligation}</li>
 *   <li>Normalización de texto: eliminación de tildes y caracteres no-ASCII antes de persistir</li>
 *   <li>Auditoría: registrar resultado final (éxito / parcial)</li>
 * </ol>
 *
 * <p><b>Integridad transaccional:</b>
 * La persistencia de datos corre bajo una única transacción {@code @Transactional}.
 * Los eventos de auditoría usan {@code Propagation.REQUIRES_NEW} en {@link AuditService},
 * de modo que siempre se persisten independientemente del resultado de la transacción principal.
 */
@Service
public class BulkImportApplicationService {

    private static final Logger log = LoggerFactory.getLogger(BulkImportApplicationService.class);

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.BASIC_ISO_DATE; // yyyyMMdd

    // ── Dependencias ──────────────────────────────────────────────────────────
    private final CsvAssociateParser               csvParser;
    private final TxtAssociateParser               txtParser;
    private final AssociateImportValidationService validationService;
    private final ClientRepository                clientRepository;
    private final ObligationRepository            obligationRepository;
    private final AuditService                    auditService;

    public BulkImportApplicationService(CsvAssociateParser csvParser,
                                          TxtAssociateParser txtParser,
                                          AssociateImportValidationService validationService,
                                          ClientRepository clientRepository,
                                          ObligationRepository obligationRepository,
                                          AuditService auditService) {
        this.csvParser            = csvParser;
        this.txtParser            = txtParser;
        this.validationService    = validationService;
        this.clientRepository     = clientRepository;
        this.obligationRepository = obligationRepository;
        this.auditService         = auditService;
    }

    // ── API pública ───────────────────────────────────────────────────────────

    /**
     * Procesa un archivo CSV o TXT de carga masiva de forma síncrona y transaccional.
     *
     * <p>Si la validación estructural falla, se hace rollback total y se lanza
     * {@link BulkImportException}. Si pasa la validación, se procesan fila a fila
     * con upsert en {@code Cliente} y {@code Obligacion}; los errores individuales
     * se recolectan y se devuelven en el resumen sin cancelar el proceso completo.
     *
     * <p><b>Correlation ID:</b> al inicio se genera un UUID único que queda
     * almacenado en {@link AuditContext} (basado en {@link ThreadLocal}). Todos
     * los eventos de auditoría emitidos durante la ejecución lo heredan
     * automáticamente, permitiendo filtrar en BD por un único
     * {@code id_auditoria} todos los eventos de este flujo (UPLOAD_STARTED,
     * UPLOAD_FAILED/UPLOAD_PARTIAL/UPLOAD_SUCCESS). El contexto se limpia en un
     * bloque {@code finally} para evitar fugas en el pool de hilos.
     *
     * @param file       archivo CSV o TXT con los registros a importar
     * @param uploadedBy nombre/ID del usuario que ejecuta la carga (para auditoría)
     * @return resumen con estadísticas del proceso
     * @throws BulkImportException si el archivo es inválido o la validación falla
     */
    @Transactional(rollbackFor = Exception.class)
    public BulkImportResultResponse procesarCarga(MultipartFile file, String uploadedBy) {
        String fileName = sanitizeFileName(file.getOriginalFilename());

        // ── Se genera el correlation ID del flujo completo ────────────────────
        // Un único UUID por ejecución del método, compartido por todos los
        // eventos de auditoría que se registren dentro.
        UUID correlationId = AuditContext.startNewContext();
        log.info("[CARGA-MASIVA] Iniciando: archivo='{}', usuario='{}', tamaño={}B, correlationId={}",
                fileName, uploadedBy, file.getSize(), correlationId);

        try {
            // ── PASO 1: Auditoría — inicio (transacción propia) ───────────────
            auditService.registerEvent(
                    "INTEGRATION", "CARGA_MASIVA", null,
                    "UPLOAD_STARTED", uploadedBy, "SISTEMA", "WEB",
                    "Inicio de carga masiva del archivo '%s' (%d bytes)"
                            .formatted(fileName, file.getSize())
            );

            // ── PASO 2: Parse streaming (CSV o TXT según extensión) ───────────
            List<AssociateRowDTO> rows = parsearArchivo(file, fileName);
            log.info("[CARGA-MASIVA] Parseadas {} filas de '{}'", rows.size(), fileName);

            // ── PASO 3: Validación estructural (servicio de dominio) ──────────
            List<RowErrorDTO> erroresValidacion = new ArrayList<>(validationService.validateAll(rows));

            if (!erroresValidacion.isEmpty()) {
                log.warn("[CARGA-MASIVA] Validación fallida: {} errores en '{}'",
                        erroresValidacion.size(), fileName);

                // Auditoría de fallo — transacción propia, sobrevive al rollback
                auditService.registerEvent(
                        "INTEGRATION", "CARGA_MASIVA", null,
                        "UPLOAD_FAILED", uploadedBy, "SISTEMA", "WEB",
                        "Archivo '%s' rechazado: %d errores de validación en %d filas"
                                .formatted(fileName, erroresValidacion.size(), rows.size())
                );
                throw new BulkImportException(erroresValidacion, rows.size(), fileName);
            }

            // ── PASO 4: Upsert fila por fila ──────────────────────────────────
            // Cache en memoria: numDocumento → clientId (evita N+1 queries)
            Map<String, Long> cacheClientes = new HashMap<>();

            List<RowErrorDTO> erroresFila = new ArrayList<>();
            int exitosos = 0;

            for (AssociateRowDTO row : rows) {
                try {
                    Long clienteId = upsertCliente(row, cacheClientes);
                    upsertObligacion(row, clienteId);
                    exitosos++;
                } catch (Exception ex) {
                    log.warn("[CARGA-MASIVA] Error en fila {}, doc='{}': {}",
                            row.rowNumber(), row.numDocumento(), ex.getMessage());
                    erroresFila.add(RowErrorDTO.row(row.rowNumber(),
                            "Error procesando documento '%s': %s"
                                    .formatted(row.numDocumento(), ex.getMessage())));
                }
            }

            // ── PASO 5: Auditoría — resultado final ───────────────────────────
            String accion  = erroresFila.isEmpty() ? "UPLOAD_SUCCESS" : "UPLOAD_PARTIAL";
            String detalle = "Archivo '%s': %d/%d registros procesados, %d errores."
                    .formatted(fileName, exitosos, rows.size(), erroresFila.size());

            auditService.registerEvent(
                    "INTEGRATION", "CARGA_MASIVA", 2L,
                    accion, uploadedBy, "SISTEMA", "WEB", detalle
            );

            log.info("[CARGA-MASIVA] Finalizado: archivo='{}', exitosos={}, fallidos={}, total={}",
                    fileName, exitosos, erroresFila.size(), rows.size());

            return BulkImportResultResponse.partial(rows.size(), exitosos, fileName, erroresFila);
        } finally {
            // SIEMPRE limpiar el ThreadLocal para evitar fugas en el pool de hilos.
            AuditContext.clear();
        }
    }

    // ── Upsert Cliente ────────────────────────────────────────────────────────

    /**
     * Crea o actualiza un cliente a partir de la fila del archivo.
     *
     * <p>Los campos de texto se normalizan antes de persistir (tildes → sin tilde).
     * El {@code numDocumento} y el {@code tipoId} se usan como clave de búsqueda
     * en mayúsculas para garantizar unicidad independiente de la capitalización.
     *
     * <p>Utiliza el cache de la sesión para evitar consultas repetidas cuando el
     * mismo documento aparece más de una vez en el archivo.
     *
     * @param row           fila validada del archivo
     * @param cacheClientes cache {numDocumento → clientId} de la sesión actual
     * @return ID del cliente creado o actualizado
     */
    private Long upsertCliente(AssociateRowDTO row, Map<String, Long> cacheClientes) {
        String docKey = row.numDocumento().trim().toUpperCase();

        // Si ya lo procesamos en esta misma carga, reusar ID del cache
        if (cacheClientes.containsKey(docKey)) {
            Long idEnCache = cacheClientes.get(docKey);
            clientRepository.findById(idEnCache).ifPresent(c -> {
                c.updateFromBatch(
                        normalize(row.nombreCompleto()),
                        normalize(row.telefono1()),
                        nullableField(normalize(row.email())),
                        nullableField(normalize(row.telefono2())),
                        nullableField(normalize(row.ciudad())),
                        nullableField(row.canalPreferido())   // canal: no normalizar (case-sensitive)
                );
                clientRepository.save(c);
            });
            return idEnCache;
        }

        Optional<Client> existente = clientRepository.findByDocumento(
                row.tipoId().trim().toUpperCase(),
                docKey
        );

        Client cliente;
        if (existente.isPresent()) {
            // ── Actualizar ────────────────────────────────────────────────────
            cliente = existente.get();
            cliente.updateFromBatch(
                    normalize(row.nombreCompleto()),
                    normalize(row.telefono1()),
                    nullableField(normalize(row.email())),
                    nullableField(normalize(row.telefono2())),
                    nullableField(normalize(row.ciudad())),
                    nullableField(row.canalPreferido())
            );
            log.debug("[CARGA-MASIVA] Cliente actualizado: doc='{}'", docKey);
        } else {
            // ── Crear ─────────────────────────────────────────────────────────
            cliente = Client.create(
                    row.tipoId().trim().toUpperCase(),
                    docKey,
                    normalize(row.nombreCompleto())
            );
            cliente.updateFromBatch(
                    normalize(row.nombreCompleto()),
                    normalize(row.telefono1()),
                    nullableField(normalize(row.email())),
                    nullableField(normalize(row.telefono2())),
                    nullableField(normalize(row.ciudad())),
                    nullableField(row.canalPreferido())
            );
            log.debug("[CARGA-MASIVA] Cliente creado: doc='{}'", docKey);
        }

        Client guardado = clientRepository.save(cliente);
        cacheClientes.put(docKey, guardado.getId());
        return guardado.getId();
    }

    // ── Upsert Obligación ─────────────────────────────────────────────────────

    /**
     * Crea o actualiza una obligación a partir de la fila del archivo.
     *
     * <p>Si {@code numObligacion} no existe → crea y vincula al {@code clienteId}.
     * Si existe → actualiza saldo, días de mora, fecha de vencimiento,
     * segmento, producto y código de agente.
     *
     * @param row       fila validada del archivo
     * @param clienteId ID del cliente al que pertenece esta obligación
     */
    private void upsertObligacion(AssociateRowDTO row, Long clienteId) {
        BigDecimal saldoTotal = new BigDecimal(row.saldoTotalRaw().trim().replace(",", "."));
        int        diasMora   = Integer.parseInt(row.diasMoraRaw().trim());
        LocalDate  fechaVenc  = LocalDate.parse(row.fechaVencRaw().trim(), DATE_FMT);

        String segmento     = nullableField(normalize(row.segmento()));
        String producto     = nullableField(normalize(row.producto()));
        String codigoAgente = nullableField(normalize(row.codigoAgente()));

        Optional<Obligation> existente =
                obligationRepository.findByObligationNumber(row.numObligacion().trim());

        if (existente.isPresent()) {
            Obligation obligacion = existente.get();
            obligacion.updateFromBatch(saldoTotal, diasMora, fechaVenc,
                    segmento, producto, codigoAgente);
            obligationRepository.save(obligacion);
            log.debug("[CARGA-MASIVA] Obligación actualizada: num='{}'", row.numObligacion());
        } else {
            Obligation nueva = Obligation.create(clienteId, row.numObligacion().trim(), saldoTotal);
            nueva.updateFromBatch(saldoTotal, diasMora, fechaVenc,
                    segmento, producto, codigoAgente);
            obligationRepository.save(nueva);
            log.debug("[CARGA-MASIVA] Obligación creada: num='{}', cliente={}",
                    row.numObligacion(), clienteId);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Delega el parse al parser correcto según la extensión del archivo.
     *
     * @param file     archivo recibido
     * @param fileName nombre limpio del archivo (para mensajes de error)
     * @return lista de filas parseadas
     * @throws BulkImportException si el archivo no puede ser leído o tiene formato inválido
     */
    private List<AssociateRowDTO> parsearArchivo(MultipartFile file, String fileName) {
        try {
            String ext = extension(fileName);
            if ("txt".equals(ext)) {
                log.debug("[CARGA-MASIVA] Usando TxtAssociateParser para '{}'", fileName);
                return txtParser.parse(file.getInputStream());
            }
            // Por defecto: CSV
            log.debug("[CARGA-MASIVA] Usando CsvAssociateParser para '{}'", fileName);
            return csvParser.parse(file.getInputStream());
        } catch (IOException e) {
            log.error("[CARGA-MASIVA] Error leyendo '{}': {}", fileName, e.getMessage());
            throw new BulkImportException("No se pudo leer el archivo: " + e.getMessage(), fileName);
        } catch (IllegalArgumentException e) {
            throw new BulkImportException(e.getMessage(), fileName);
        }
    }

    /** Extrae la extensión en minúsculas de un nombre de archivo. */
    private String extension(String fileName) {
        if (fileName == null) return "";
        int dot = fileName.lastIndexOf('.');
        return dot >= 0 ? fileName.substring(dot + 1).toLowerCase() : "";
    }

    /** Sanitiza el nombre de archivo: elimina trayectorias maliciosas. */
    private String sanitizeFileName(String originalFileName) {
        if (originalFileName == null || originalFileName.isBlank()) {
            return "archivo_sin_nombre";
        }
        // Tomar solo el nombre base, sin directorios
        String name = originalFileName
                .replace("\\", "/")
                .replaceAll(".*/", "");
        return name.isBlank() ? "archivo_sin_nombre" : name;
    }

    /**
     * Aplica normalización de texto ({@link TextUtils#limpiarTexto}) a un campo.
     * Si el campo es null, retorna null sin lanzar excepción.
     */
    private String normalize(String value) {
        return TextUtils.limpiarTexto(value);
    }

    /** Convierte cadena vacía en null; deja null como null y texto con contenido intacto. */
    private String nullableField(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
