package coovitelCobranza.cobranzas.obligacion.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ObligationTest {

    @Test
    void deberiaRegisterDelinquencyYUpdateStatus() {
        Obligation obligacion = Obligation.create(10L, "OBL-001", new BigDecimal("150000"));

        obligacion.registerDelinquency(15, new BigDecimal("50000"));

        assertEquals(15, obligacion.getDelinquencyDays());
        assertEquals(Obligation.StatusObligation.EN_MORA, obligacion.getStatus());
        assertEquals(new BigDecimal("50000"), obligacion.getOverdueBalance());
    }

    @Test
    void deberiaLanzarErrorSiPaymentEsCeroONegativo() {
        Obligation obligacion = Obligation.create(10L, "OBL-001", new BigDecimal("150000"));

        assertThrows(IllegalArgumentException.class, () -> obligacion.applyPayment(BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> obligacion.applyPayment(new BigDecimal("-1")));
    }
}

