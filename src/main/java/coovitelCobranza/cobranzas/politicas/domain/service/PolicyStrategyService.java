package coovitelCobranza.cobranzas.politicas.domain.service;

public interface PolicyStrategyService {

    String definirSiguienteAction(Long obligacionId, Long clienteId, int diasDelinquency, String canalSugerido);

    boolean permiteContact(String canal, int horaDia, int intentosPrevios);
}

