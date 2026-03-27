package cooviteCobranza.cobranzas.casogestion.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests - CasoGestion Domain Model")
class CasoGestionTest {

    @Test
    @DisplayName("Crear caso gestion abierto")
    void testCrearCasoGestion() {
        // Act
        CasoGestion caso = CasoGestion.crear(1L, CasoGestion.Prioridad.ALTA);

        // Assert
        assertNotNull(caso);
        assertNull(caso.getId());
        assertEquals(1L, caso.getObligacionId());
        assertEquals(CasoGestion.Prioridad.ALTA, caso.getPrioridad());
        assertEquals(CasoGestion.Estado.ABIERTO, caso.getEstado());
        assertNull(caso.getAsesorAsignado());
        assertNull(caso.getProximaAccionAt());
    }

    @Test
    @DisplayName("Crear caso requiere obligacionId")
    void testCrearCasoSinObligacion() {
        assertThrows(NullPointerException.class, () -> {
            CasoGestion.crear(null, CasoGestion.Prioridad.MEDIA);
        });
    }

    @Test
    @DisplayName("Crear caso requiere prioridad")
    void testCrearCasoSinPrioridad() {
        assertThrows(NullPointerException.class, () -> {
            CasoGestion.crear(1L, null);
        });
    }

    @Test
    @DisplayName("Asignar asesor cambia estado a EN_GESTION")
    void testAsignarAsesor() {
        // Arrange
        CasoGestion caso = CasoGestion.crear(1L, CasoGestion.Prioridad.MEDIA);

        // Act
        caso.asignarAsesor("Juan García");

        // Assert
        assertEquals("Juan García", caso.getAsesorAsignado());
        assertEquals(CasoGestion.Estado.EN_GESTION, caso.getEstado());
    }

    @Test
    @DisplayName("Asignar asesor vacio lanza excepcion")
    void testAsignarAsesorVacio() {
        // Arrange
        CasoGestion caso = CasoGestion.crear(1L, CasoGestion.Prioridad.MEDIA);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            caso.asignarAsesor("");
        });
    }

    @Test
    @DisplayName("Asignar asesor nulo lanza excepcion")
    void testAsignarAsesorNulo() {
        // Arrange
        CasoGestion caso = CasoGestion.crear(1L, CasoGestion.Prioridad.MEDIA);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            caso.asignarAsesor(null);
        });
    }

    @Test
    @DisplayName("Programar siguiente accion")
    void testProgramarAccion() {
        // Arrange
        CasoGestion caso = CasoGestion.crear(1L, CasoGestion.Prioridad.ALTA);
        LocalDateTime fecha = LocalDateTime.now().plusDays(1);

        // Act
        caso.programarSiguienteAccion(fecha);

        // Assert
        assertEquals(fecha, caso.getProximaAccionAt());
    }

    @Test
    @DisplayName("Programar accion nula lanza excepcion")
    void testProgramarAccionNula() {
        // Arrange
        CasoGestion caso = CasoGestion.crear(1L, CasoGestion.Prioridad.MEDIA);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            caso.programarSiguienteAccion(null);
        });
    }

    @Test
    @DisplayName("Cerrar caso")
    void testCerrarCaso() {
        // Arrange
        CasoGestion caso = CasoGestion.crear(1L, CasoGestion.Prioridad.BAJA);

        // Act
        caso.cerrar();

        // Assert
        assertEquals(CasoGestion.Estado.CERRADO, caso.getEstado());
    }

    @Test
    @DisplayName("Prioridades disponibles")
    void testPrioridades() {
        assertEquals(4, CasoGestion.Prioridad.values().length);
        assertNotNull(CasoGestion.Prioridad.BAJA);
        assertNotNull(CasoGestion.Prioridad.MEDIA);
        assertNotNull(CasoGestion.Prioridad.ALTA);
        assertNotNull(CasoGestion.Prioridad.CRITICA);
    }

    @Test
    @DisplayName("Estados disponibles")
    void testEstados() {
        assertEquals(4, CasoGestion.Estado.values().length);
        assertNotNull(CasoGestion.Estado.ABIERTO);
        assertNotNull(CasoGestion.Estado.EN_GESTION);
        assertNotNull(CasoGestion.Estado.PAUSADO);
        assertNotNull(CasoGestion.Estado.CERRADO);
    }

    @Test
    @DisplayName("Reconstruir caso desde estado persistido")
    void testReconstruirCaso() {
        // Arrange
        LocalDateTime ahora = LocalDateTime.now();

        // Act
        CasoGestion caso = CasoGestion.reconstruir(
                1L,
                1L,
                CasoGestion.Prioridad.ALTA,
                CasoGestion.Estado.EN_GESTION,
                "María López",
                ahora.plusDays(1),
                ahora
        );

        // Assert
        assertEquals(1L, caso.getId());
        assertEquals(1L, caso.getObligacionId());
        assertEquals(CasoGestion.Prioridad.ALTA, caso.getPrioridad());
        assertEquals(CasoGestion.Estado.EN_GESTION, caso.getEstado());
        assertEquals("María López", caso.getAsesorAsignado());
    }

    @Test
    @DisplayName("Getters retornan valores correctos")
    void testGetters() {
        // Arrange & Act
        CasoGestion caso = CasoGestion.crear(5L, CasoGestion.Prioridad.CRITICA);

        // Assert
        assertNull(caso.getId());
        assertEquals(5L, caso.getObligacionId());
        assertEquals(CasoGestion.Prioridad.CRITICA, caso.getPrioridad());
        assertNotNull(caso.getUpdatedAt());
    }

    @Test
    @DisplayName("UpdatedAt se actualiza en operaciones")
    void testUpdatedAtSeActualiza() throws InterruptedException {
        // Arrange
        CasoGestion caso = CasoGestion.crear(1L, CasoGestion.Prioridad.MEDIA);
        LocalDateTime antes = caso.getUpdatedAt();

        Thread.sleep(100);

        // Act
        caso.asignarAsesor("Carlos");

        // Assert
        assertTrue(caso.getUpdatedAt().isAfter(antes));
    }
}

