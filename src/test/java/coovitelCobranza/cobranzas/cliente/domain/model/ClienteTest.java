package coovitelCobranza.cobranzas.cliente.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests - Cliente Domain Model")
class ClienteTest {

    @Test
    @DisplayName("Crear cliente valido con factory method")
    void testCrearClienteValido() {
        // Arrange & Act
        Cliente cliente = Cliente.crear("CC", "12345678", "Juan Pérez");

        // Assert
        assertNotNull(cliente);
        assertNull(cliente.getId());
        assertEquals("CC", cliente.getTipoDocumento());
        assertEquals("12345678", cliente.getNumeroDocumento());
        assertEquals("Juan Pérez", cliente.getNombreCompleto());
        assertNull(cliente.getTelefono());
        assertNull(cliente.getEmail());
        assertFalse(cliente.isAceptaWhatsApp());
        assertFalse(cliente.isAceptaSms());
        assertFalse(cliente.isAceptaEmail());
    }

    @Test
    @DisplayName("Crear cliente requiere tipoDocumento no nulo")
    void testCrearClienteSinTipoDocumento() {
        // Assert - NullPointerException esperada
        assertThrows(NullPointerException.class, () -> {
            Cliente.crear(null, "12345678", "Juan Pérez");
        });
    }

    @Test
    @DisplayName("Crear cliente requiere numeroDocumento no nulo")
    void testCrearClienteSinNumeroDocumento() {
        // Assert
        assertThrows(NullPointerException.class, () -> {
            Cliente.crear("CC", null, "Juan Pérez");
        });
    }

    @Test
    @DisplayName("Crear cliente requiere nombreCompleto no nulo")
    void testCrearClienteSinNombreCompleto() {
        // Assert
        assertThrows(NullPointerException.class, () -> {
            Cliente.crear("CC", "12345678", null);
        });
    }

    @Test
    @DisplayName("Reconstruir cliente desde estado persistido")
    void testReconstruirCliente() {
        // Arrange
        var ahora = java.time.LocalDateTime.now();
        
        // Act
        Cliente cliente = Cliente.reconstruir(
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
        assertEquals("Juan Pérez", cliente.getNombreCompleto());
        assertEquals("3001234567", cliente.getTelefono());
        assertEquals("juan@example.com", cliente.getEmail());
        assertTrue(cliente.isAceptaWhatsApp());
        assertFalse(cliente.isAceptaSms());
        assertTrue(cliente.isAceptaEmail());
        assertEquals(ahora, cliente.getUpdatedAt());
    }

    @Test
    @DisplayName("Actualizar contacto cambia telefono y email")
    void testActualizarContacto() {
        // Arrange
        Cliente cliente = Cliente.crear("CC", "12345678", "Juan Pérez");
        var updatedAtAntes = cliente.getUpdatedAt();

        // Act
        cliente.actualizarContacto("3009876543", "juan.nuevo@example.com");

        // Assert
        assertEquals("3009876543", cliente.getTelefono());
        assertEquals("juan.nuevo@example.com", cliente.getEmail());
        assertTrue(cliente.getUpdatedAt().isAfter(updatedAtAntes));
    }

    @Test
    @DisplayName("Actualizar contacto con valores nulos es permitido")
    void testActualizarContactoConNulos() {
        // Arrange
        Cliente cliente = Cliente.crear("CC", "12345678", "Juan Pérez");

        // Act - No debe lanzar excepción
        cliente.actualizarContacto(null, null);

        // Assert
        assertNull(cliente.getTelefono());
        assertNull(cliente.getEmail());
    }

    @Test
    @DisplayName("Actualizar consentimientos cambia todas las banderas")
    void testActualizarConsentimientos() {
        // Arrange
        Cliente cliente = Cliente.crear("CC", "12345678", "Juan Pérez");
        var updatedAtAntes = cliente.getUpdatedAt();

        // Act
        cliente.actualizarConsentimientos(true, true, false);

        // Assert
        assertTrue(cliente.isAceptaWhatsApp());
        assertTrue(cliente.isAceptaSms());
        assertFalse(cliente.isAceptaEmail());
        assertTrue(cliente.getUpdatedAt().isAfter(updatedAtAntes));
    }

    @Test
    @DisplayName("Actualizar consentimientos a false")
    void testActualizarConsentimientosAFalse() {
        // Arrange
        Cliente cliente = Cliente.reconstruir(
                1L, "CC", "12345678", "Juan Pérez",
                "3001234567", "juan@example.com",
                true, true, true, java.time.LocalDateTime.now()
        );

        // Act
        cliente.actualizarConsentimientos(false, false, false);

        // Assert
        assertFalse(cliente.isAceptaWhatsApp());
        assertFalse(cliente.isAceptaSms());
        assertFalse(cliente.isAceptaEmail());
    }

    @Test
    @DisplayName("Getters retornan valores correctos")
    void testGetters() {
        // Arrange & Act
        Cliente cliente = Cliente.crear("TI", "9876543", "Carlos López");

        // Assert
        assertNull(cliente.getId());
        assertEquals("TI", cliente.getTipoDocumento());
        assertEquals("9876543", cliente.getNumeroDocumento());
        assertEquals("Carlos López", cliente.getNombreCompleto());
        assertNotNull(cliente.getUpdatedAt());
    }

    @Test
    @DisplayName("UpdatedAt se actualiza en cada operación de escritura")
    void testUpdatedAtSeActualiza() throws InterruptedException {
        // Arrange
        Cliente cliente = Cliente.crear("CC", "123", "Test");
        var timestamp1 = cliente.getUpdatedAt();
        
        Thread.sleep(100); // Pequeña pausa para asegurar diferencia de tiempo

        // Act
        cliente.actualizarContacto("3001234567", "test@example.com");
        var timestamp2 = cliente.getUpdatedAt();

        // Assert
        assertTrue(timestamp2.isAfter(timestamp1));
    }
}

