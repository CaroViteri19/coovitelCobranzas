package coovitelCobranza.cobranzas.integration.application.service;

public interface DebtPortfolioIngestionService {

    void processEventApi(String idempotencyKey, String payloadJson);

    void processBatch(String loteId, String rutaArchivo);
}

