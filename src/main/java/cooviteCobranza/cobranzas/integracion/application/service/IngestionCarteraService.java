package cooviteCobranza.cobranzas.integracion.application.service;

public interface IngestionCarteraService {

    void procesarEventoApi(String idempotencyKey, String payloadJson);

    void procesarLoteBatch(String loteId, String rutaArchivo);
}

