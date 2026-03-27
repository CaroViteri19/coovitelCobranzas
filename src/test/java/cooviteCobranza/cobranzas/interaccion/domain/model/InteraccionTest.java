package cooviteCobranza.cobranzas.interaccion.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests - Interaccion Domain Model")
class InteraccionTest {

    @Test
    @DisplayName("Crear interaccion pendiente valida")
    void testCrearInteraccionPendiente() {
        // Act
        Interaccion inter = Interaccion.crear(1L, Interaccion.Canal.WHATSAPP, "Hola cliente");

        // Assert
        assertNotNull(inter);
        assertNull(inter.getId());
        assertEquals(1L, inter.getCasoGestionId());
        assertEquals(Interaccion.Canal.WHATSAPP, inter.getCanal());
        assertEquals("Hola cliente", inter.getPlantilla());
        assertEquals(Interaccion.EstadoResultado.PENDIENTE, inter.getResultado());
    }

    @Test
    @DisplayName("Crear interaccion requiere casoGestionId")
    void testCrearInteraccionSinCaso() {
        assertThrows(NullPointerException.class, () -> {
            Interaccion.crear(null, Interaccion.Canal.SMS, "Test");
        });
    }

    @Test
    @DisplayName("Crear interaccion requiere canal")
    void testCrearInteraccionSinCanal() {
        assertThrows(NullPointerException.class, () -> {
            Interaccion.crear(1L, null, "Test");
        });
    }

    @Test
    @DisplayName("Marcar interaccion como entregada")
    void testMarcarEntregada() {
        // Arrange
        Interaccion inter = Interaccion.crear(1L, Interaccion.Canal.SMS, "Test");

        // Act
        inter.marcarEntregada();

        // Assert
        assertEquals(Interaccion.EstadoResultado.ENTREGADA, inter.getResultado());
    }

    @Test
    @DisplayName("Marcar interaccion como leida")
    void testMarcarLeida() {
        // Arrange
        Interaccion inter = Interaccion.crear(1L, Interaccion.Canal.EMAIL, "Test");

        // Act
        inter.marcarLeida();

        // Assert
        assertEquals(Interaccion.EstadoResultado.LEIDA, inter.getResultado());
    }

    @Test
    @DisplayName("Marcar interaccion como fallida")
    void testMarcarFallida() {
        // Arrange
        Interaccion inter = Interaccion.crear(1L, Interaccion.Canal.VOZ, "Test");

        // Act
        inter.marcarFallida();

        // Assert
        assertEquals(Interaccion.EstadoResultado.FALLIDA, inter.getResultado());
    }

    @Test
    @DisplayName("Canales disponibles")
    void testCanalesDisponibles() {
        assertEquals(4, Interaccion.Canal.values().length);
        assertNotNull(Interaccion.Canal.SMS);
        assertNotNull(Interaccion.Canal.WHATSAPP);
        assertNotNull(Interaccion.Canal.EMAIL);
        assertNotNull(Interaccion.Canal.VOZ);
    }

    @Test
    @DisplayName("Estados de resultado disponibles")
    void testEstadosResultadoDisponibles() {
        assertTrue(Interaccion.EstadoResultado.values().length >= 5);
    }

    @Test
    @DisplayName("Getters retornan valores correctos")
    void testGetters() {
        // Arrange & Act
        Interaccion inter = Interaccion.crear(5L, Interaccion.Canal.WHATSAPP, "Test message");

        // Assert
        assertEquals(5L, inter.getCasoGestionId());
        assertEquals(Interaccion.Canal.WHATSAPP, inter.getCanal());
        assertEquals("Test message", inter.getPlantilla());
        assertNotNull(inter.getCreatedAt());
    }
}

