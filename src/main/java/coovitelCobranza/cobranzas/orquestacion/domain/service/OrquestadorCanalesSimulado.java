package coovitelCobranza.cobranzas.orquestacion.domain.service;

import coovitelCobranza.cobranzas.interaccion.domain.model.Interaccion;
import org.springframework.stereotype.Component;

@Component
public class OrquestadorCanalesSimulado implements OrquestadorCanales {

    @Override
    public Interaccion enviar(Long casoGestionId, Interaccion.Canal canal, String plantilla, String destino) {
        Interaccion interaccion = Interaccion.crear(casoGestionId, canal, plantilla + " -> " + destino);
        interaccion.marcarEntregada();
        return interaccion;
    }
}

