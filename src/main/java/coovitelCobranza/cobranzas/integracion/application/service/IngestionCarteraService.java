package coovitelCobranza.cobranzas.integracion.application.service;

public interface IngestionCarteraService {

    void procesarEventApi(String idempotencyKey, String payloadJson);

    void procesarLoteBatch(String loteId, String rutaArchivo);
}

