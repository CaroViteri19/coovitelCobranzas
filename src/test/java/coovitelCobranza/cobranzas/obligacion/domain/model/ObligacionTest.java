package coovitelCobranza.cobranzas.obligacion.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ObligacionTest {

    @Test
    void deberiaRegistrarMoraYActualizarEstado() {
        Obligacion obligacion = Obligacion.crear(10L, "OBL-001", new BigDecimal("150000"));

        obligacion.registrarMora(15, new BigDecimal("50000"));

        assertEquals(15, obligacion.getDiasMora());
        assertEquals(Obligacion.EstadoObligacion.EN_MORA, obligacion.getEstado());
        assertEquals(new BigDecimal("50000"), obligacion.getSaldoVencido());
    }

    @Test
    void deberiaLanzarErrorSiPagoEsCeroONegativo() {
        Obligacion obligacion = Obligacion.crear(10L, "OBL-001", new BigDecimal("150000"));

        assertThrows(IllegalArgumentException.class, () -> obligacion.aplicarPago(BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> obligacion.aplicarPago(new BigDecimal("-1")));
    }
}

