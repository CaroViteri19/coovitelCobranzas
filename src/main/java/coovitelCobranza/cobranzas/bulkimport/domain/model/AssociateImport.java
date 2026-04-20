package coovitelCobranza.cobranzas.bulkimport.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Aggregate root del dominio de Carga Masiva.
 * Representa un registro de deudor/asociado importado desde un archivo CSV.
 *
 * <p>Diseñado como inmutable excepto el ID (asignado en persistencia).
 * Toda creación pasa por los factory methods {@link #create} o {@link #reconstruct}.
 *
 * @author BankVision – Coovitel
 * @since 1.0
 */
public class AssociateImport {

    // ── Identificador ──────────────────────────────────────────────────────────
    private Long id;

    // ── Campos obligatorios ────────────────────────────────────────────────────
    private final String tipoId;          // CC | NIT | CE | PA
    private final String numDocumento;    // VARCHAR(20)
    private final String nombreCompleto;  // VARCHAR(120)
    private final String numObligacion;   // VARCHAR(30)
    private final BigDecimal saldoTotal;  // DECIMAL(18,2)
    private final int diasMora;
    private final LocalDate fechaVenc;
    private final String telefono1;       // VARCHAR(15)

    // ── Campos opcionales ─────────────────────────────────────────────────────
    private final String email;           // VARCHAR(80)  nullable
    private final String telefono2;       // VARCHAR(15)  nullable
    private final String ciudad;          // VARCHAR(60)  nullable
    private final String canalPreferido;  // VARCHAR(20)  nullable
    private final String segmento;        // VARCHAR(30)  nullable
    private final String producto;        // VARCHAR(50)  nullable
    private final String codigoAgente;    // VARCHAR(10)  nullable

    // ── Metadatos ─────────────────────────────────────────────────────────────
    private final LocalDateTime createdAt;

    // ── Constructor privado ───────────────────────────────────────────────────
    private AssociateImport(Long id,
                           String tipoId, String numDocumento, String nombreCompleto,
                           String numObligacion, BigDecimal saldoTotal, int diasMora,
                           LocalDate fechaVenc, String telefono1,
                           String email, String telefono2, String ciudad,
                           String canalPreferido, String segmento, String producto,
                           String codigoAgente, LocalDateTime createdAt) {
        this.id             = id;
        this.tipoId         = Objects.requireNonNull(tipoId,         "tipoId es obligatorio");
        this.numDocumento   = Objects.requireNonNull(numDocumento,   "numDocumento es obligatorio");
        this.nombreCompleto = Objects.requireNonNull(nombreCompleto, "nombreCompleto es obligatorio");
        this.numObligacion  = Objects.requireNonNull(numObligacion,  "numObligacion es obligatorio");
        this.saldoTotal     = Objects.requireNonNull(saldoTotal,     "saldoTotal es obligatorio");
        this.diasMora       = diasMora;
        this.fechaVenc      = Objects.requireNonNull(fechaVenc,      "fechaVenc es obligatoria");
        this.telefono1      = Objects.requireNonNull(telefono1,      "telefono1 es obligatorio");
        this.email          = email;
        this.telefono2      = telefono2;
        this.ciudad         = ciudad;
        this.canalPreferido = canalPreferido;
        this.segmento       = segmento;
        this.producto       = producto;
        this.codigoAgente   = codigoAgente;
        this.createdAt      = Objects.requireNonNullElseGet(createdAt, LocalDateTime::now);
    }

    // ── Factory: nuevo registro (sin ID) ──────────────────────────────────────
    /**
     * Crea un nuevo asociado para inserción. El ID será asignado por la BD.
     */
    public static AssociateImport create(String tipoId, String numDocumento,
                                        String nombreCompleto, String numObligacion,
                                        BigDecimal saldoTotal, int diasMora,
                                        LocalDate fechaVenc, String telefono1,
                                        String email, String telefono2, String ciudad,
                                        String canalPreferido, String segmento,
                                        String producto, String codigoAgente) {
        return new AssociateImport(
                null, tipoId, numDocumento, nombreCompleto, numObligacion,
                saldoTotal, diasMora, fechaVenc, telefono1,
                email, telefono2, ciudad, canalPreferido, segmento, producto,
                codigoAgente, LocalDateTime.now()
        );
    }

    // ── Factory: reconstrucción desde persistencia ────────────────────────────
    public static AssociateImport reconstruct(Long id, String tipoId, String numDocumento,
                                             String nombreCompleto, String numObligacion,
                                             BigDecimal saldoTotal, int diasMora,
                                             LocalDate fechaVenc, String telefono1,
                                             String email, String telefono2, String ciudad,
                                             String canalPreferido, String segmento,
                                             String producto, String codigoAgente,
                                             LocalDateTime createdAt) {
        return new AssociateImport(
                id, tipoId, numDocumento, nombreCompleto, numObligacion,
                saldoTotal, diasMora, fechaVenc, telefono1,
                email, telefono2, ciudad, canalPreferido, segmento, producto,
                codigoAgente, createdAt
        );
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public Long getId()              { return id; }
    public String getTipoId()        { return tipoId; }
    public String getNumDocumento()  { return numDocumento; }
    public String getNombreCompleto(){ return nombreCompleto; }
    public String getNumObligacion() { return numObligacion; }
    public BigDecimal getSaldoTotal(){ return saldoTotal; }
    public int getDiasMora()         { return diasMora; }
    public LocalDate getFechaVenc()  { return fechaVenc; }
    public String getTelefono1()     { return telefono1; }
    public String getEmail()         { return email; }
    public String getTelefono2()     { return telefono2; }
    public String getCiudad()        { return ciudad; }
    public String getCanalPreferido(){ return canalPreferido; }
    public String getSegmento()      { return segmento; }
    public String getProducto()      { return producto; }
    public String getCodigoAgente()  { return codigoAgente; }
    public LocalDateTime getCreatedAt(){ return createdAt; }

    // ── Setter de ID (asignado tras persistencia) ─────────────────────────────
    public void assignId(Long id) {
        if (this.id != null) throw new IllegalStateException("El ID ya fue asignado");
        this.id = Objects.requireNonNull(id, "ID no puede ser null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AssociateImport a)) return false;
        return Objects.equals(numDocumento, a.numDocumento) &&
               Objects.equals(numObligacion, a.numObligacion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numDocumento, numObligacion);
    }

    @Override
    public String toString() {
        return "AssociateImport{doc=%s, obligacion=%s, nombre=%s}"
                .formatted(numDocumento, numObligacion, nombreCompleto);
    }
}
