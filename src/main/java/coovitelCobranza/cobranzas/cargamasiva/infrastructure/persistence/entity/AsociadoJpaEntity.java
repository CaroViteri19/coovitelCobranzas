package coovitelCobranza.cobranzas.cargamasiva.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad JPA para la tabla {@code asociados_carga}.
 *
 * <p>Usada para:
 * <ul>
 *   <li>Queries de unicidad ({@link AsociadoJpaRepository})</li>
 *   <li>Lectura posterior de registros cargados</li>
 * </ul>
 *
 * <p>La inserción masiva se realiza via {@code JdbcTemplate.batchUpdate()}
 * para evitar el overhead de Hibernate con {@code GenerationType.IDENTITY}.
 */
@Entity
@Table(
    name = "asociados_carga",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_asociado_documento", columnNames = "num_documento")
    },
    indexes = {
        @Index(name = "idx_asociado_dias_mora",     columnList = "dias_mora"),
        @Index(name = "idx_asociado_saldo",         columnList = "saldo_total"),
        @Index(name = "idx_asociado_fecha_venc",    columnList = "fecha_venc"),
        @Index(name = "idx_asociado_segmento",      columnList = "segmento"),
        @Index(name = "idx_asociado_codigo_agente", columnList = "codigo_agente"),
        @Index(name = "idx_asociado_email",         columnList = "email")
    }
)
public class AsociadoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asociado")
    private Long idAsociado;

    // ── Campos obligatorios ──────────────────────────────────────────────────
    @Column(name = "tipo_id", nullable = false, length = 2)
    private String tipoId;

    @Column(name = "num_documento", nullable = false, unique = true, length = 20)
    private String numDocumento;

    @Column(name = "nombre_completo", nullable = false, length = 120)
    private String nombreCompleto;

    @Column(name = "num_obligacion", nullable = false, length = 30)
    private String numObligacion;

    @Column(name = "saldo_total", nullable = false, precision = 18, scale = 2)
    private BigDecimal saldoTotal;

    @Column(name = "dias_mora", nullable = false)
    private int diasMora;

    @Column(name = "fecha_venc", nullable = false)
    private LocalDate fechaVenc;

    @Column(name = "telefono_1", nullable = false, length = 15)
    private String telefono1;

    // ── Campos opcionales ────────────────────────────────────────────────────
    @Column(name = "email", length = 80)
    private String email;

    @Column(name = "telefono_2", length = 15)
    private String telefono2;

    @Column(name = "ciudad", length = 60)
    private String ciudad;

    @Column(name = "canal_preferido", length = 20)
    private String canalPreferido;

    @Column(name = "segmento", length = 30)
    private String segmento;

    @Column(name = "producto", length = 50)
    private String producto;

    @Column(name = "codigo_agente", length = 10)
    private String codigoAgente;

    // ── Metadatos ────────────────────────────────────────────────────────────
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── Lifecycle callbacks ──────────────────────────────────────────────────
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ── Constructor vacío requerido por JPA ───────────────────────────────────
    public AsociadoJpaEntity() {}

    // ── Getters y Setters ─────────────────────────────────────────────────────
    public Long getIdAsociado()        { return idAsociado; }
    public String getTipoId()          { return tipoId; }
    public void setTipoId(String v)    { this.tipoId = v; }
    public String getNumDocumento()    { return numDocumento; }
    public void setNumDocumento(String v) { this.numDocumento = v; }
    public String getNombreCompleto()  { return nombreCompleto; }
    public void setNombreCompleto(String v) { this.nombreCompleto = v; }
    public String getNumObligacion()   { return numObligacion; }
    public void setNumObligacion(String v)  { this.numObligacion = v; }
    public BigDecimal getSaldoTotal()  { return saldoTotal; }
    public void setSaldoTotal(BigDecimal v) { this.saldoTotal = v; }
    public int getDiasMora()           { return diasMora; }
    public void setDiasMora(int v)     { this.diasMora = v; }
    public LocalDate getFechaVenc()    { return fechaVenc; }
    public void setFechaVenc(LocalDate v)   { this.fechaVenc = v; }
    public String getTelefono1()       { return telefono1; }
    public void setTelefono1(String v) { this.telefono1 = v; }
    public String getEmail()           { return email; }
    public void setEmail(String v)     { this.email = v; }
    public String getTelefono2()       { return telefono2; }
    public void setTelefono2(String v) { this.telefono2 = v; }
    public String getCiudad()          { return ciudad; }
    public void setCiudad(String v)    { this.ciudad = v; }
    public String getCanalPreferido()  { return canalPreferido; }
    public void setCanalPreferido(String v) { this.canalPreferido = v; }
    public String getSegmento()        { return segmento; }
    public void setSegmento(String v)  { this.segmento = v; }
    public String getProducto()        { return producto; }
    public void setProducto(String v)  { this.producto = v; }
    public String getCodigoAgente()    { return codigoAgente; }
    public void setCodigoAgente(String v)  { this.codigoAgente = v; }
    public LocalDateTime getCreatedAt(){ return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public LocalDateTime getUpdatedAt(){ return updatedAt; }
}
