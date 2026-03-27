package coovitelCobranza.cobranzas.orquestacion.domain.service;

import coovitelCobranza.cobranzas.interaccion.domain.model.Interaccion;

public interface OrquestadorCanales {

    Interaccion enviar(Long casoGestionId, Interaccion.Canal canal, String plantilla, String destino);
}

