package coovitelCobranza.cobranzas.client.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests - Client Domain Model")
class ClientTest {

    @Test
    @DisplayName("Create cliente valido con factory method")
    void testCreateClientValido() {
        // Arrange & Act
        Client cliente = Client.create("CC", "12345678", "Juan Pérez");

        // Assert
        assertNotNull(cliente);
        assertNull(cliente.getId());
        assertEquals("CC", cliente.getTipoDocumento());
        assertEquals("12345678", cliente.getNumeroDocumento());
        assertEquals("Juan Pérez", cliente.getFullName());
        assertNull(cliente.getTelefono());
        assertNull(cliente.getEmail());
        assertFalse(cliente.isAceptaWhatsApp());
        assertFalse(cliente.isAceptaSms());
        assertFalse(cliente.isAceptaEmail());
    }

    @Test
    @DisplayName("Create cliente requiere tipoDocumento no nulo")
    void testCreateClientSinTipoDocumento() {
        // Assert - NullPointerException esperada
        assertThrows(NullPointerException.class, () -> {
            Client.create(null, "12345678", "Juan Pérez");
        });
    }

    @Test
    @DisplayName("Create cliente requiere numeroDocumento no nulo")
    void testCreateClientSinNumeroDocumento() {
        // Assert
        assertThrows(NullPointerException.class, () -> {
            Client.create("CC", null, "Juan Pérez");
        });
    }

    @Test
    @DisplayName("Create cliente requiere nameCompleto no nulo")
    void testCreateClientSinNombreCompleto() {
        // Assert
        assertThrows(NullPointerException.class, () -> {
            Client.create("CC", "12345678", null);
        });
    }

    @Test
    @DisplayName("Reconstruir cliente desde estado persistido")
    void testReconstruirClient() {
        // Arrange
        var ahora = java.time.LocalDateTime.now();
        
        // Act
        Client cliente = Client.reconstruct(
                1L,
                "CC",
                "12345678",
                "Juan Pérez",
                "3001234567",
                "juan@example.com",
                true,
                false,
                true,
                ahora
        );

        // Assert
        assertEquals(1L, cliente.getId());
        assertEquals("CC", cliente.getTipoDocumento());
        assertEquals("12345678", cliente.getNumeroDocumento());
        assertEquals("Juan Pérez", cliente.getFullName());
        assertEquals("3001234567", cliente.getTelefono());
        assertEquals("juan@example.com", cliente.getEmail());
        assertTrue(cliente.isAceptaWhatsApp());
        assertFalse(cliente.isAceptaSms());
        assertTrue(cliente.isAceptaEmail());
        assertEquals(ahora, cliente.getUpdatedAt());
    }

    @Test
    @DisplayName("Update contacto cambia telefono y email")
    void testUpdateContact() {
        // Arrange
        Client cliente = Client.create("CC", "12345678", "Juan Pérez");
        var updatedAtAntes = cliente.getUpdatedAt();

        // Act
        cliente.updateContact("3009876543", "juan.nuevo@example.com");

        // Assert
        assertEquals("3009876543", cliente.getTelefono());
        assertEquals("juan.nuevo@example.com", cliente.getEmail());
        assertTrue(cliente.getUpdatedAt().isAfter(updatedAtAntes));
    }

    @Test
    @DisplayName("Update contacto con valores nulos es permitido")
    void testUpdateContactConNulos() {
        // Arrange
        Client cliente = Client.create("CC", "12345678", "Juan Pérez");

        // Act - No debe lanzar excepción
        cliente.updateContact(null, null);

        // Assert
        assertNull(cliente.getTelefono());
        assertNull(cliente.getEmail());
    }

    @Test
    @DisplayName("Update consentimientos cambia todas las banderas")
    void testUpdateConsents() {
        // Arrange
        Client cliente = Client.create("CC", "12345678", "Juan Pérez");
        var updatedAtAntes = cliente.getUpdatedAt();

        // Act
        cliente.updateConsents(true, true, false);

        // Assert
        assertTrue(cliente.isAceptaWhatsApp());
        assertTrue(cliente.isAceptaSms());
        assertFalse(cliente.isAceptaEmail());
        assertTrue(cliente.getUpdatedAt().isAfter(updatedAtAntes));
    }

    @Test
    @DisplayName("Update consentimientos a false")
    void testUpdateConsentsAFalse() {
        // Arrange
        Client cliente = Client.reconstruct(
                1L, "CC", "12345678", "Juan Pérez",
                "3001234567", "juan@example.com",
                true, true, true, java.time.LocalDateTime.now()
        );

        // Act
        cliente.updateConsents(false, false, false);

        // Assert
        assertFalse(cliente.isAceptaWhatsApp());
        assertFalse(cliente.isAceptaSms());
        assertFalse(cliente.isAceptaEmail());
    }

    @Test
    @DisplayName("Getters retornan valores correctos")
    void testGetters() {
        // Arrange & Act
        Client cliente = Client.create("TI", "9876543", "Carlos López");

        // Assert
        assertNull(cliente.getId());
        assertEquals("TI", cliente.getTipoDocumento());
        assertEquals("9876543", cliente.getNumeroDocumento());
        assertEquals("Carlos López", cliente.getFullName());
        assertNotNull(cliente.getUpdatedAt());
    }

    @Test
    @DisplayName("UpdatedAt se actualiza en cada operación de escritura")
    void testUpdatedAtSeActualiza() throws InterruptedException {
        // Arrange
        Client cliente = Client.create("CC", "123", "Test");
        var timestamp1 = cliente.getUpdatedAt();
        
        Thread.sleep(100); // Pequeña pausa para asegurar diferencia de tiempo

        // Act
        cliente.updateContact("3001234567", "test@example.com");
        var timestamp2 = cliente.getUpdatedAt();

        // Assert
        assertTrue(timestamp2.isAfter(timestamp1));
    }
}

