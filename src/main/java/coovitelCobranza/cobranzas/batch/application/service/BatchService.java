package coovitelCobranza.cobranzas.batch.application.service;

import coovitelCobranza.cobranzas.batch.application.dto.BatchErrorDetail;
import coovitelCobranza.cobranzas.batch.application.dto.BatchResultResponse;
import coovitelCobranza.cobranzas.batch.application.dto.CargaBatchDTO;
import coovitelCobranza.cobranzas.batch.infrastructure.parser.CsvBatchParser;
import coovitelCobranza.cobranzas.batch.infrastructure.parser.TxtBatchParser;
import coovitelCobranza.cobranzas.cliente.infrastructure.persistence.entity.ClientJpaEntity;
import coovitelCobranza.cobranzas.cliente.infrastructure.persistence.repository.ClientJpaRepository;
import coovitelCobranza.cobranzas.obligacion.infrastructure.persistence.entity.ObligationJpaEntity;
import coovitelCobranza.cobranzas.obligacion.infrastructure.persistence.repository.ObligationJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio principal del proceso batch.
 *
 * Flujo:
 *  1. Detectar tipo de archivo (CSV / TXT) por extensión.
 *  2. Parsear el archivo por streaming.
 *  3. Por cada fila válida:
 *     a. Upsert de cliente (por NUM_DOCUMENTO).
 *     b. Upsert de obligación (por NUM_OBLIGACION).
 *  4. Retornar resumen con totales y errores.
 *
 * Optimizaciones:
 *  - Cache en memoria (HashMap) de clientes ya procesados en la misma carga,
 *    para evitar consultas repetitivas a BD por el mismo NUM_DOCUMENTO.
 *  - Errores por fila no detienen el proceso completo.
 */
@Service
public class BatchService {

    private static final Logger log = LoggerFactory.getLogger(BatchService.class);

    private final CsvBatchParser csvParser;
    private final TxtBatchParser txtParser;
    private final ClientJpaRepository clientRepo;
    private final ObligationJpaRepository obligationRepo;

    public BatchService(CsvBatchParser csvParser,
                        TxtBatchParser txtParser,
                        ClientJpaRepository clientRepo,
                        ObligationJpaRepository obligationRepo) {
        this.csvParser     = csvParser;
        this.txtParser     = txtParser;
        this.clientRepo    = clientRepo;
        this.obligationRepo = obligationRepo;
    }

    // ── Punto de entrada ──────────────────────────────────────────────────────

    @Transactional
    public BatchResultResponse procesarArchivo(MultipartFile archivo) throws Exception {
        String nombreArchivo = archivo.getOriginalFilename() != null
                ? archivo.getOriginalFilename().toLowerCase()
                : "";

        log.info("Iniciando carga batch del archivo: {}", nombreArchivo);

        List<String> erroresParseo = new ArrayList<>();
        List<CargaBatchDTO> filas;

        if (nombreArchivo.endsWith(".csv")) {
            filas = csvParser.parse(archivo.getInputStream(), erroresParseo);
        } else if (nombreArchivo.endsWith(".txt")) {
            filas = txtParser.parse(archivo.getInputStream(), erroresParseo);
        } else {
            throw new IllegalArgumentException(
                    "Tipo de archivo no soportado. Se aceptan únicamente .csv y .txt");
        }

        // Convertir errores de parseo a BatchErrorDetail (sin número de fila exacto
        // porque ya viene incluido en el mensaje del parser)
        List<BatchErrorDetail> errores = new ArrayList<>();
        for (String msg : erroresParseo) {
            errores.add(new BatchErrorDetail(extraerFila(msg), msg));
        }

        int exitosos = 0;
        int fallidos = erroresParseo.size(); // filas que fallaron en parseo

        // Cache de clientes procesados en esta carga: numDocumento → clientId
        Map<String, Long> cacheClientes = new HashMap<>();

        // Procesar filas válidas
        for (CargaBatchDTO dto : filas) {
            try {
                Long clienteId = upsertCliente(dto, cacheClientes);
                upsertObligacion(dto, clienteId);
                exitosos++;
            } catch (Exception ex) {
                fallidos++;
                log.warn("Error procesando fila con documento {}: {}", dto.getNumDocumento(), ex.getMessage());
                errores.add(new BatchErrorDetail(0, "Documento " + dto.getNumDocumento() + ": " + ex.getMessage()));
            }
        }

        int total = filas.size() + erroresParseo.size();
        log.info("Carga batch finalizada. Total: {}, Exitosos: {}, Fallidos: {}", total, exitosos, fallidos);

        return new BatchResultResponse(total, exitosos, fallidos, errores);
    }

    // ── Upsert Cliente ────────────────────────────────────────────────────────

    private Long upsertCliente(CargaBatchDTO dto, Map<String, Long> cache) {
        // Primero verificar cache de esta sesión de carga
        if (cache.containsKey(dto.getNumDocumento())) {
            // El cliente ya fue creado/actualizado en esta misma carga,
            // igual aplicamos actualización para reflejar posibles cambios entre filas
            Long idEnCache = cache.get(dto.getNumDocumento());
            clientRepo.findById(idEnCache).ifPresent(e -> {
                actualizarCamposCliente(e, dto);
                clientRepo.save(e);
            });
            return idEnCache;
        }

        Optional<ClientJpaEntity> existente = clientRepo.findByNumeroDocumento(dto.getNumDocumento());

        if (existente.isPresent()) {
            ClientJpaEntity e = existente.get();
            actualizarCamposCliente(e, dto);
            ClientJpaEntity guardado = clientRepo.save(e);
            cache.put(dto.getNumDocumento(), guardado.getId());
            log.debug("Cliente actualizado: documento={}", dto.getNumDocumento());
            return guardado.getId();
        } else {
            ClientJpaEntity nuevo = new ClientJpaEntity();
            nuevo.setTipoDocumento(dto.getTipoId());
            nuevo.setNumeroDocumento(dto.getNumDocumento());
            nuevo.setFullName(dto.getNombreCompleto());
            nuevo.setTelefono(dto.getTelefono1());
            nuevo.setTelefono2(dto.getTelefono2());
            nuevo.setEmail(dto.getEmail());
            nuevo.setCiudad(dto.getCiudad());
            nuevo.setCanalPreferido(dto.getCanalPreferido());
            nuevo.setAceptaWhatsApp(false);
            nuevo.setAceptaSms(false);
            nuevo.setAceptaEmail(false);
            nuevo.setUpdatedAt(LocalDateTime.now());
            ClientJpaEntity guardado = clientRepo.save(nuevo);
            cache.put(dto.getNumDocumento(), guardado.getId());
            log.debug("Cliente creado: documento={}", dto.getNumDocumento());
            return guardado.getId();
        }
    }

    private void actualizarCamposCliente(ClientJpaEntity e, CargaBatchDTO dto) {
        e.setFullName(dto.getNombreCompleto());
        e.setTelefono(dto.getTelefono1());
        if (dto.getTelefono2() != null)    e.setTelefono2(dto.getTelefono2());
        if (dto.getEmail() != null)        e.setEmail(dto.getEmail());
        if (dto.getCiudad() != null)       e.setCiudad(dto.getCiudad());
        if (dto.getCanalPreferido() != null) e.setCanalPreferido(dto.getCanalPreferido());
        e.setUpdatedAt(LocalDateTime.now());
    }

    // ── Upsert Obligación ─────────────────────────────────────────────────────

    private void upsertObligacion(CargaBatchDTO dto, Long clienteId) {
        Optional<ObligationJpaEntity> existente =
                obligationRepo.findByObligationNumber(dto.getNumObligacion());

        if (existente.isPresent()) {
            ObligationJpaEntity e = existente.get();
            e.setTotalBalance(dto.getSaldoTotal());
            e.setOverdueDays(dto.getDiasMora());
            e.setDueDate(dto.getFechaVenc());
            if (dto.getSegmento() != null)     e.setSegmento(dto.getSegmento());
            if (dto.getProducto() != null)     e.setProducto(dto.getProducto());
            if (dto.getCodigoAgente() != null) e.setCodigoAgente(dto.getCodigoAgente());
            obligationRepo.save(e);
            log.debug("Obligación actualizada: numero={}", dto.getNumObligacion());
        } else {
            ObligationJpaEntity nueva = new ObligationJpaEntity();
            nueva.setCustomerId(clienteId);
            nueva.setObligationNumber(dto.getNumObligacion());
            nueva.setTotalBalance(dto.getSaldoTotal());
            nueva.setOverdueDays(dto.getDiasMora());
            nueva.setDueDate(dto.getFechaVenc());
            nueva.setStatus(dto.getDiasMora() != null && dto.getDiasMora() > 0 ? 2 : 1); // EN_MORA o AL_DIA
            nueva.setSegmento(dto.getSegmento());
            nueva.setProducto(dto.getProducto());
            nueva.setCodigoAgente(dto.getCodigoAgente());
            nueva.setCreatedAt(LocalDateTime.now());
            obligationRepo.save(nueva);
            log.debug("Obligación creada: numero={}", dto.getNumObligacion());
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Extrae el número de fila del mensaje de error generado por los parsers.
     * Formato esperado: "Fila N: ..."
     */
    private int extraerFila(String mensaje) {
        try {
            if (mensaje.startsWith("Fila ")) {
                String[] partes = mensaje.split(":");
                return Integer.parseInt(partes[0].replace("Fila ", "").trim());
            }
        } catch (Exception ignored) {}
        return 0;
    }
}
