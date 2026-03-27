package cooviteCobranza.cobranzas.orquestacion.domain.service;

import cooviteCobranza.cobranzas.interaccion.domain.model.Interaccion;

public interface OrquestadorCanales {

    Interaccion enviar(Long casoGestionId, Interaccion.Canal canal, String plantilla, String destino);
}

