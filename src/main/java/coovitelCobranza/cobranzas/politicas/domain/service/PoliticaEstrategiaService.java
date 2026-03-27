package coovitelCobranza.cobranzas.politicas.domain.service;

public interface PoliticaEstrategiaService {

    String definirSiguienteAccion(Long obligacionId, Long clienteId, int diasMora, String canalSugerido);

    boolean permiteContacto(String canal, int horaDia, int intentosPrevios);
}

