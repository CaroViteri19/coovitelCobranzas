package coovitelCobranza.cobranzas.bulkimport.domain.service;

import coovitelCobranza.cobranzas.bulkimport.application.dto.AssociateRowDTO;
import coovitelCobranza.cobranzas.bulkimport.application.dto.RowErrorDTO;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Servicio de dominio para validación de registros CSV importados.
 *
 * <p><b>Estrategia de validación en dos fases:</b>
 * <ol>
 *   <li>Fase 1 – Validación estructural por fila (campos obligatorios, tipos, formatos)</li>
 *   <li>Fase 2 – Validación de unicidad dentro del propio archivo</li>
 * </ol>
 *
 * <p>La validación contra la BD (unicidad global) ocurre en el Application Service.
 * Esta clase es pura y sin dependencias de infraestructura (testeable sin Spring).
 */
@Service
public class AssociateImportValidationService {

    // ── Constantes de validación ──────────────────────────────────────────────
    private static final Set<String> TIPOS_ID_VALIDOS =
            Set.of("CC", "NIT", "CE", "PA");

    private static final Set<String> CANALES_VALIDOS =
            Set.of("WhatsApp", "SMS", "Email", "Voz");

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern FECHA_PATTERN =
            Pattern.compile("^\\d{8}$"); // YYYYMMDD


    private static final int MAX_FILE_ROWS  = 50_000;

    // ── API pública ───────────────────────────────────────────────────────────

    /**
     * Valida todos los registros parseados del CSV.
     * Recolecta TODOS los errores antes de retornar (no fail-fast).
     *
     * @param rows lista de filas parseadas
     * @return lista de errores; vacía si todo es válido
     */
    public List<RowErrorDTO> validateAll(List<AssociateRowDTO> rows) {
        List<RowErrorDTO> errors = new ArrayList<>();

        if (rows.isEmpty()) {
            errors.add(RowErrorDTO.global("El archivo CSV no contiene registros de datos."));
            return errors;
        }
        if (rows.size() > MAX_FILE_ROWS) {
            errors.add(RowErrorDTO.global(
                    "El archivo excede el máximo permitido de %d registros (tiene %d)."
                    .formatted(MAX_FILE_ROWS, rows.size())));
            return errors;
        }

        // Fase 1: validación por fila
        for (AssociateRowDTO row : rows) {
            errors.addAll(validateRow(row));
        }

        // Fase 2: unicidad dentro del archivo
        if (errors.isEmpty()) {
            errors.addAll(validateIntraFileUniqueness(rows));
        }

        return errors;
    }

    // ── Validación estructural por fila ───────────────────────────────────────

    private List<RowErrorDTO> validateRow(AssociateRowDTO row) {
        List<RowErrorDTO> errors = new ArrayList<>();
        int r = row.rowNumber();

        // TIPO_ID
        if (isBlank(row.tipoId())) {
            errors.add(RowErrorDTO.field(r, "TIPO_ID", "Campo obligatorio vacío."));
        } else if (!TIPOS_ID_VALIDOS.contains(row.tipoId().toUpperCase())) {
            errors.add(RowErrorDTO.field(r, "TIPO_ID",
                    "Valor inválido '%s'. Permitidos: CC, NIT, CE, PA.".formatted(row.tipoId())));
        }

        // NUM_DOCUMENTO
        if (isBlank(row.numDocumento())) {
            errors.add(RowErrorDTO.field(r, "NUM_DOCUMENTO", "Campo obligatorio vacío."));
        } else if (row.numDocumento().length() > 20) {
            errors.add(RowErrorDTO.field(r, "NUM_DOCUMENTO",
                    "Supera la longitud máxima de 20 caracteres."));
        }

        // NOMBRE_COMPLETO
        if (isBlank(row.nombreCompleto())) {
            errors.add(RowErrorDTO.field(r, "NOMBRE_COMPLETO", "Campo obligatorio vacío."));
        } else if (row.nombreCompleto().length() > 120) {
            errors.add(RowErrorDTO.field(r, "NOMBRE_COMPLETO",
                    "Supera la longitud máxima de 120 caracteres."));
        }

        // NUM_OBLIGACION
        if (isBlank(row.numObligacion())) {
            errors.add(RowErrorDTO.field(r, "NUM_OBLIGACION", "Campo obligatorio vacío."));
        } else if (row.numObligacion().length() > 30) {
            errors.add(RowErrorDTO.field(r, "NUM_OBLIGACION",
                    "Supera la longitud máxima de 30 caracteres."));
        }

        // SALDO_TOTAL
        if (isBlank(row.saldoTotalRaw())) {
            errors.add(RowErrorDTO.field(r, "SALDO_TOTAL", "Campo obligatorio vacío."));
        } else {
            try {
                var val = new java.math.BigDecimal(row.saldoTotalRaw().replace(",", "."));
                if (val.scale() > 2) {
                    errors.add(RowErrorDTO.field(r, "SALDO_TOTAL",
                            "Máximo 2 decimales permitidos."));
                }
            } catch (NumberFormatException e) {
                errors.add(RowErrorDTO.field(r, "SALDO_TOTAL",
                        "Formato numérico inválido: '%s'.".formatted(row.saldoTotalRaw())));
            }
        }

        // DIAS_MORA
        if (isBlank(row.diasMoraRaw())) {
            errors.add(RowErrorDTO.field(r, "DIAS_MORA", "Campo obligatorio vacío."));
        } else {
            try {
                int dias = Integer.parseInt(row.diasMoraRaw().trim());
                if (dias < 0) {
                    errors.add(RowErrorDTO.field(r, "DIAS_MORA",
                            "Los días de mora no pueden ser negativos."));
                }
            } catch (NumberFormatException e) {
                errors.add(RowErrorDTO.field(r, "DIAS_MORA",
                        "Valor entero inválido: '%s'.".formatted(row.diasMoraRaw())));
            }
        }

        // FECHA_VENC (YYYYMMDD)
        if (isBlank(row.fechaVencRaw())) {
            errors.add(RowErrorDTO.field(r, "FECHA_VENC", "Campo obligatorio vacío."));
        } else if (!FECHA_PATTERN.matcher(row.fechaVencRaw().trim()).matches()) {
            errors.add(RowErrorDTO.field(r, "FECHA_VENC",
                    "Formato de fecha inválido '%s'. Esperado: YYYYMMDD."
                    .formatted(row.fechaVencRaw())));
        } else {
            try {
                java.time.LocalDate.parse(row.fechaVencRaw().trim(),
                        java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
            } catch (java.time.format.DateTimeParseException e) {
                errors.add(RowErrorDTO.field(r, "FECHA_VENC",
                        "Fecha inválida: '%s'.".formatted(row.fechaVencRaw())));
            }
        }

        // TELEFONO_1
        if (isBlank(row.telefono1())) {
            errors.add(RowErrorDTO.field(r, "TELEFONO_1", "Campo obligatorio vacío."));
        } else if (row.telefono1().length() > 15) {
            errors.add(RowErrorDTO.field(r, "TELEFONO_1",
                    "Supera la longitud máxima de 15 caracteres."));
        }

        // EMAIL (opcional pero con formato si está presente)
        if (!isBlank(row.email())) {
            if (row.email().length() > 80) {
                errors.add(RowErrorDTO.field(r, "EMAIL",
                        "Supera la longitud máxima de 80 caracteres."));
            } else if (!EMAIL_PATTERN.matcher(row.email().trim()).matches()) {
                errors.add(RowErrorDTO.field(r, "EMAIL",
                        "Formato de email inválido: '%s'.".formatted(row.email())));
            }
        }

        // CANAL_PREFERIDO (opcional pero con valor controlado si está presente)
        if (!isBlank(row.canalPreferido()) && !CANALES_VALIDOS.contains(row.canalPreferido().trim())) {
            errors.add(RowErrorDTO.field(r, "CANAL_PREFERIDO",
                    "Valor inválido '%s'. Permitidos: WhatsApp, SMS, Email, Voz."
                    .formatted(row.canalPreferido())));
        }

        return errors;
    }

    // ── Validación de unicidad intra-archivo ──────────────────────────────────

    /**
     * Detecta documentos y emails duplicados dentro del mismo archivo.
     */
    private List<RowErrorDTO> validateIntraFileUniqueness(List<AssociateRowDTO> rows) {
        List<RowErrorDTO> errors = new ArrayList<>();
        Set<String> seenDocumentos = new HashSet<>();
        Set<String> seenEmails    = new HashSet<>();

        for (AssociateRowDTO row : rows) {
            int r = row.rowNumber();

            if (!isBlank(row.numDocumento())) {
                String doc = row.numDocumento().trim().toUpperCase();
                if (!seenDocumentos.add(doc)) {
                    errors.add(RowErrorDTO.field(r, "NUM_DOCUMENTO",
                            "Documento '%s' duplicado dentro del archivo.".formatted(doc)));
                }
            }

            if (!isBlank(row.email())) {
                String email = row.email().trim().toLowerCase();
                if (!seenEmails.add(email)) {
                    errors.add(RowErrorDTO.field(r, "EMAIL",
                            "Email '%s' duplicado dentro del archivo.".formatted(email)));
                }
            }
        }

        return errors;
    }

    // ── Utilidades privadas ───────────────────────────────────────────────────

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
