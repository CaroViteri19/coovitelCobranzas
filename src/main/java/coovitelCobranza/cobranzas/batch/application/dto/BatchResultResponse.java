package coovitelCobranza.cobranzas.batch.application.dto;

import java.util.List;

/**
 * Respuesta del proceso batch.
 * {
 *   "totalRegistros": number,
 *   "exitosos": number,
 *   "fallidos": number,
 *   "errores": [ { "fila": number, "mensaje": "..." } ]
 * }
 */
public class BatchResultResponse {

    private int totalRegistros;
    private int exitosos;
    private int fallidos;
    private List<BatchErrorDetail> errores;

    public BatchResultResponse() {}

    public BatchResultResponse(int totalRegistros, int exitosos, int fallidos,
                               List<BatchErrorDetail> errores) {
        this.totalRegistros = totalRegistros;
        this.exitosos       = exitosos;
        this.fallidos       = fallidos;
        this.errores        = errores;
    }

    public int getTotalRegistros() { return totalRegistros; }
    public void setTotalRegistros(int totalRegistros) { this.totalRegistros = totalRegistros; }

    public int getExitosos() { return exitosos; }
    public void setExitosos(int exitosos) { this.exitosos = exitosos; }

    public int getFallidos() { return fallidos; }
    public void setFallidos(int fallidos) { this.fallidos = fallidos; }

    public List<BatchErrorDetail> getErrores() { return errores; }
    public void setErrores(List<BatchErrorDetail> errores) { this.errores = errores; }
}
