package coovitelCobranza.cobranzas.cliente.application.service;

import coovitelCobranza.cobranzas.cliente.application.dto.ActualizarContactoClienteRequest;
import coovitelCobranza.cobranzas.cliente.application.dto.ActualizarConsentimientosClienteRequest;
import coovitelCobranza.cobranzas.cliente.application.dto.ClienteResponse;
import coovitelCobranza.cobranzas.cliente.application.dto.CrearClienteRequest;
import coovitelCobranza.cobranzas.cliente.application.exception.ClienteBusinessException;
import coovitelCobranza.cobranzas.cliente.application.exception.ClienteNotFoundException;
import coovitelCobranza.cobranzas.cliente.domain.repository.ClienteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("Integration Tests - ClienteApplicationService")
class ClienteApplicationServiceIntegrationTest {

    @Autowired
    private ClienteApplicationService clienteApplicationService;

    @Autowired
    private ClienteRepository clienteRepository;

    @Test
    @DisplayName("Crear cliente exitoso")
    void testCrearClienteExitoso() {
        // Arrange
        CrearClienteRequest request = new CrearClienteRequest(
                "CC",
                "123456789",
                "Juan Pérez",
                "3001234567",
                "juan@example.com"
        );

        // Act
        ClienteResponse response = clienteApplicationService.crearCliente(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals("CC", response.tipoDocumento());
        assertEquals("123456789", response.numeroDocumento());
        assertEquals("Juan Pérez", response.nombreCompleto());
        assertEquals("3001234567", response.telefono());
        assertEquals("juan@example.com", response.email());
    }

    @Test
    @DisplayName("Crear cliente con documento duplicado lanza excepcion")
    void testCrearClienteDocumentoDuplicado() {
        // Arrange
        CrearClienteRequest request1 = new CrearClienteRequest("CC", "111", "Cliente1", null, null);
        CrearClienteRequest request2 = new CrearClienteRequest("CC", "111", "Cliente2", null, null);

        // Act & Assert
        clienteApplicationService.crearCliente(request1);
        assertThrows(ClienteBusinessException.class, () -> {
            clienteApplicationService.crearCliente(request2);
        });
    }

    @Test
    @DisplayName("Obtener cliente por ID")
    void testObtenerClientePorId() {
        // Arrange
        CrearClienteRequest request = new CrearClienteRequest("CC", "222", "Test User", null, null);
        ClienteResponse creado = clienteApplicationService.crearCliente(request);

        // Act
        ClienteResponse obtenido = clienteApplicationService.obtenerPorId(creado.id());

        // Assert
        assertNotNull(obtenido);
        assertEquals(creado.id(), obtenido.id());
        assertEquals("Test User", obtenido.nombreCompleto());
    }

    @Test
    @DisplayName("Obtener cliente inexistente lanza excepcion")
    void testObtenerClienteInexistente() {
        // Act & Assert
        assertThrows(ClienteNotFoundException.class, () -> {
            clienteApplicationService.obtenerPorId(99999L);
        });
    }

    @Test
    @DisplayName("Obtener cliente por documento")
    void testObtenerClientePorDocumento() {
        // Arrange
        CrearClienteRequest request = new CrearClienteRequest("TI", "333", "Carlos", null, null);
        clienteApplicationService.crearCliente(request);

        // Act
        ClienteResponse response = clienteApplicationService.obtenerPorDocumento("TI", "333");

        // Assert
        assertNotNull(response);
        assertEquals("Carlos", response.nombreCompleto());
    }

    @Test
    @DisplayName("Actualizar contacto cliente")
    void testActualizarContactoCliente() {
        // Arrange
        CrearClienteRequest crear = new CrearClienteRequest("CC", "444", "María", null, null);
        ClienteResponse creado = clienteApplicationService.crearCliente(crear);

        ActualizarContactoClienteRequest actualizar = new ActualizarContactoClienteRequest(
                "3009876543",
                "maria@example.com"
        );

        // Act
        ClienteResponse actualizado = clienteApplicationService.actualizarContacto(creado.id(), actualizar);

        // Assert
        assertEquals("3009876543", actualizado.telefono());
        assertEquals("maria@example.com", actualizado.email());
    }

    @Test
    @DisplayName("Actualizar consentimientos cliente")
    void testActualizarConsentimientosCliente() {
        // Arrange
        CrearClienteRequest crear = new CrearClienteRequest("CC", "555", "Pedro", null, null);
        ClienteResponse creado = clienteApplicationService.crearCliente(crear);

        ActualizarConsentimientosClienteRequest consentimientos = new ActualizarConsentimientosClienteRequest(
                true,
                false,
                true
        );

        // Act
        ClienteResponse actualizado = clienteApplicationService.actualizarConsentimientos(creado.id(), consentimientos);

        // Assert
        assertTrue(actualizado.aceptaWhatsApp());
        assertFalse(actualizado.aceptaSms());
        assertTrue(actualizado.aceptaEmail());
    }

    @Test
    @DisplayName("Actualizar cliente inexistente lanza excepcion")
    void testActualizarClienteInexistente() {
        // Arrange
        ActualizarContactoClienteRequest request = new ActualizarContactoClienteRequest("123", "test@test.com");

        // Act & Assert
        assertThrows(ClienteNotFoundException.class, () -> {
            clienteApplicationService.actualizarContacto(99999L, request);
        });
    }

    @Test
    @DisplayName("Flujo completo: Crear, obtener y actualizar cliente")
    void testFlujoCompletoCliente() {
        // Act 1: Crear
        CrearClienteRequest crear = new CrearClienteRequest("CE", "666", "Ana López", "3001111111", "ana@test.com");
        ClienteResponse creado = clienteApplicationService.crearCliente(crear);

        // Assert 1
        assertNotNull(creado.id());
        assertEquals("Ana López", creado.nombreCompleto());

        // Act 2: Obtener
        ClienteResponse obtenido = clienteApplicationService.obtenerPorId(creado.id());

        // Assert 2
        assertEquals(creado.id(), obtenido.id());

        // Act 3: Actualizar
        ActualizarContactoClienteRequest actualizar = new ActualizarContactoClienteRequest("3002222222", "ana.nueva@test.com");
        ClienteResponse actualizado = clienteApplicationService.actualizarContacto(creado.id(), actualizar);

        // Assert 3
        assertEquals("3002222222", actualizado.telefono());
        assertEquals("ana.nueva@test.com", actualizado.email());
    }
}

