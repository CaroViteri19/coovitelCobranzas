package coovitelCobranza.cobranzas.casogestion.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests - Case Domain Model")
class CaseTest {

    @Test
    @DisplayName("Create case open")
    void testCreateCase() {
        // Act
        Case caso = Case.crear(1L, Case.Priority.HIGH);

        // Assert
        assertNotNull(caso);
        assertNull(caso.getId());
        assertEquals(1L, caso.getObligationId());
        assertEquals(Case.Priority.HIGH, caso.getPriority());
        assertEquals(Case.Status.NEW, caso.getStatus());
        assertNull(caso.getAssignedAdvisor());
        assertNull(caso.getNextActionAt());
    }

    @Test
    @DisplayName("Create case requires obligationId")
    void testCreateCasoSinObligation() {
        assertThrows(NullPointerException.class, () -> Case.crear(null, Case.Priority.MEDIUM));
    }

    @Test
    @DisplayName("Create case requires priority")
    void testCreateCasoSinPriority() {
        assertThrows(NullPointerException.class, () -> Case.crear(1L, null));
    }

    @Test
    @DisplayName("Assign advisor changes status to IN_MANAGEMENT")
    void testAssignAdvisor() {
        // Arrange
        Case caso = Case.crear(1L, Case.Priority.MEDIUM);

        // Act
        caso.assignAdvisor("Juan García");

        // Assert
        assertEquals("Juan García", caso.getAssignedAdvisor());
        assertEquals(Case.Status.IN_MANAGEMENT, caso.getStatus());
    }

    @Test
    @DisplayName("Assign empty advisor throws exception")
    void testAssignAdvisorVacio() {
        // Arrange
        Case caso = Case.crear(1L, Case.Priority.MEDIUM);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> caso.assignAdvisor(""));
    }

    @Test
    @DisplayName("Assign null advisor throws exception")
    void testAssignAdvisorNulo() {
        // Arrange
        Case caso = Case.crear(1L, Case.Priority.MEDIUM);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> caso.assignAdvisor(null));
    }

    @Test
    @DisplayName("Schedule next action")
    void testScheduleAction() {
        // Arrange
        Case caso = Case.crear(1L, Case.Priority.HIGH);
        LocalDateTime fecha = LocalDateTime.now().plusDays(1);

        // Act
        caso.scheduleNextAction(fecha);

        // Assert
        assertEquals(fecha, caso.getNextActionAt());
    }

    @Test
    @DisplayName("Schedule null action throws exception")
    void testScheduleActionNula() {
        // Arrange
        Case caso = Case.crear(1L, Case.Priority.MEDIUM);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> caso.scheduleNextAction(null));
    }

    @Test
    @DisplayName("Close case")
    void testCerrarCaso() {
        // Arrange
        Case caso = Case.crear(1L, Case.Priority.LOW);
        caso.assignAdvisor("Agent 1");
        caso.transitionTo(Case.Status.PAYMENT_PROMISE, "PROMISE_ACCEPTED");

        // Act
        caso.close();

        // Assert
        assertEquals(Case.Status.CLOSED, caso.getStatus());
    }

    @Test
    @DisplayName("Available priorities")
    void testPriorityes() {
        assertEquals(4, Case.Priority.values().length);
        assertNotNull(Case.Priority.LOW);
        assertNotNull(Case.Priority.MEDIUM);
        assertNotNull(Case.Priority.HIGH);
        assertNotNull(Case.Priority.CRITICAL);
    }

    @Test
    @DisplayName("Available statuses")
    void testStatuss() {
        assertEquals(7, Case.Status.values().length);
        assertNotNull(Case.Status.NEW);
        assertNotNull(Case.Status.IN_MANAGEMENT);
        assertNotNull(Case.Status.UNREACHABLE);
        assertNotNull(Case.Status.PAYMENT_PROMISE);
        assertNotNull(Case.Status.PRE_LEGAL);
        assertNotNull(Case.Status.JUDICIAL_COLLECTION);
        assertNotNull(Case.Status.CLOSED);
    }

    @Test
    @DisplayName("Reconstruct case from persisted state")
    void testReconstruirCaso() {
        // Arrange
        LocalDateTime ahora = LocalDateTime.now();

        // Act
        Case caso = Case.reconstruct(
                1L,
                1L,
                Case.Priority.HIGH,
                Case.Status.IN_MANAGEMENT,
                "María López",
                ahora.plusDays(1),
                ahora
        );

        // Assert
        assertEquals(1L, caso.getId());
        assertEquals(1L, caso.getObligationId());
        assertEquals(Case.Priority.HIGH, caso.getPriority());
        assertEquals(Case.Status.IN_MANAGEMENT, caso.getStatus());
        assertEquals("María López", caso.getAssignedAdvisor());
    }

    @Test
    @DisplayName("Getters return correct values")
    void testGetters() {
        // Arrange & Act
        Case caso = Case.crear(5L, Case.Priority.CRITICAL);

        // Assert
        assertNull(caso.getId());
        assertEquals(5L, caso.getObligationId());
        assertEquals(Case.Priority.CRITICAL, caso.getPriority());
        assertNotNull(caso.getUpdatedAt());
    }

    @Test
    @DisplayName("UpdatedAt changes on operations")
    void testUpdatedAtSeActualiza() throws InterruptedException {
        // Arrange
        Case caso = Case.crear(1L, Case.Priority.MEDIUM);
        LocalDateTime antes = caso.getUpdatedAt();

        Thread.sleep(100);

        // Act
        caso.assignAdvisor("Carlos");

        // Assert
        assertTrue(caso.getUpdatedAt().isAfter(antes));
    }
}
