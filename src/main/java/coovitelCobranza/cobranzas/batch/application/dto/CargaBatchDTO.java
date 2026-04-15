package coovitelCobranza.cobranzas.batch.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO interno que representa una fila parseada de los archivos CSV o TXT.
 * Todos los campos son String inicialmente; la validación y conversión
 * se realiza en el servicio.
 */
public class CargaBatchDTO {

    // ── Campos requeridos ────────────────────────────────────────────────────
    private String tipoId;
    private String numDocumento;
    private String nombreCompleto;
    private String numObligacion;
    private BigDecimal saldoTotal;
    private Integer diasMora;
    private LocalDate fechaVenc;
    private String telefono1;

    // ── Campos opcionales ────────────────────────────────────────────────────
    private String email;
    private String telefono2;
    private String ciudad;
    private String canalPreferido;
    private String segmento;
    private String producto;
    private String codigoAgente;

    public CargaBatchDTO() {}

    // ── Getters y Setters ─────────────────────────────────────────────────────

    public String getTipoId() { return tipoId; }
    public void setTipoId(String tipoId) { this.tipoId = tipoId; }

    public String getNumDocumento() { return numDocumento; }
    public void setNumDocumento(String numDocumento) { this.numDocumento = numDocumento; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getNumObligacion() { return numObligacion; }
    public void setNumObligacion(String numObligacion) { this.numObligacion = numObligacion; }

    public BigDecimal getSaldoTotal() { return saldoTotal; }
    public void setSaldoTotal(BigDecimal saldoTotal) { this.saldoTotal = saldoTotal; }

    public Integer getDiasMora() { return diasMora; }
    public void setDiasMora(Integer diasMora) { this.diasMora = diasMora; }

    public LocalDate getFechaVenc() { return fechaVenc; }
    public void setFechaVenc(LocalDate fechaVenc) { this.fechaVenc = fechaVenc; }

    public String getTelefono1() { return telefono1; }
    public void setTelefono1(String telefono1) { this.telefono1 = telefono1; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono2() { return telefono2; }
    public void setTelefono2(String telefono2) { this.telefono2 = telefono2; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getCanalPreferido() { return canalPreferido; }
    public void setCanalPreferido(String canalPreferido) { this.canalPreferido = canalPreferido; }

    public String getSegmento() { return segmento; }
    public void setSegmento(String segmento) { this.segmento = segmento; }

    public String getProducto() { return producto; }
    public void setProducto(String producto) { this.producto = producto; }

    public String getCodigoAgente() { return codigoAgente; }
    public void setCodigoAgente(String codigoAgente) { this.codigoAgente = codigoAgente; }
}
