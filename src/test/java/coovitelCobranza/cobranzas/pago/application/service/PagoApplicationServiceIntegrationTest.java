package coovitelCobranza.cobranzas.pago.application.service;

import coovitelCobranza.cobranzas.pago.application.dto.ConfirmarPagoRequest;
import coovitelCobranza.cobranzas.pago.application.dto.CrearPagoRequest;
import coovitelCobranza.cobranzas.pago.application.dto.PagoResponse;
import coovitelCobranza.cobranzas.pago.application.exception.PagoBusinessException;
import coovitelCobranza.cobranzas.pago.application.exception.PagoNotFoundException;
import coovitelCobranza.cobranzas.pago.domain.repository.PagoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("Integration Tests - PagoApplicationService")
class PagoApplicationServiceIntegrationTest {

    @Autowired
    private PagoApplicationService pagoApplicationService;

    @Autowired
    private PagoRepository pagoRepository;

    @Test
    @DisplayName("Crear pago exitoso")
    void testCrearPagoExitoso() {
        // Arrange
        CrearPagoRequest request = new CrearPagoRequest(
                1L,
                new BigDecimal("500.00"),
                "PAG-001",
                "PSE"
        );

        // Act
        PagoResponse response = pagoApplicationService.crearPago(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals(1L, response.obligacionId());
        assertEquals(new BigDecimal("500.00"), response.valor());
        assertEquals("PAG-001", response.referenciaExterna());
        assertEquals("PENDIENTE", response.estado());
    }

    @Test
    @DisplayName("Crear pago con referencia duplicada lanza excepcion")
    void testCrearPagoReferenciaDuplicada() {
        // Arrange
        CrearPagoRequest request1 = new CrearPagoRequest(1L, new BigDecimal("500"), "REF-DUP", "PSE");
        CrearPagoRequest request2 = new CrearPagoRequest(1L, new BigDecimal("600"), "REF-DUP", "TARJETA");

        // Act & Assert
        pagoApplicationService.crearPago(request1);
        assertThrows(PagoBusinessException.class, () -> {
            pagoApplicationService.crearPago(request2);
        });
    }

    @Test
    @DisplayName("Crear pago con valor negativo lanza excepcion")
    void testCrearPagoValorNegativo() {
        // Arrange
        CrearPagoRequest request = new CrearPagoRequest(
                1L,
                new BigDecimal("-100.00"),
                "PAG-NEG",
                "PSE"
        );

        // Act & Assert
        assertThrows(PagoBusinessException.class, () -> {
            pagoApplicationService.crearPago(request);
        });
    }

    @Test
    @DisplayName("Crear pago con valor cero lanza excepcion")
    void testCrearPagoValorCero() {
        // Arrange
        CrearPagoRequest request = new CrearPagoRequest(1L, BigDecimal.ZERO, "PAG-ZERO", "PSE");

        // Act & Assert
        assertThrows(PagoBusinessException.class, () -> {
            pagoApplicationService.crearPago(request);
        });
    }

    @Test
    @DisplayName("Obtener pago por ID")
    void testObtenerPagoPorId() {
        // Arrange
        CrearPagoRequest crear = new CrearPagoRequest(1L, new BigDecimal("1000"), "PAG-002", "TARJETA");
        PagoResponse creado = pagoApplicationService.crearPago(crear);

        // Act
        PagoResponse obtenido = pagoApplicationService.obtenerPorId(creado.id());

        // Assert
        assertNotNull(obtenido);
        assertEquals(creado.id(), obtenido.id());
        assertEquals("PAG-002", obtenido.referenciaExterna());
    }

    @Test
    @DisplayName("Obtener pago inexistente lanza excepcion")
    void testObtenerPagoInexistente() {
        // Act & Assert
        assertThrows(PagoNotFoundException.class, () -> {
            pagoApplicationService.obtenerPorId(99999L);
        });
    }

    @Test
    @DisplayName("Obtener pago por referencia")
    void testObtenerPagoPorReferencia() {
        // Arrange
        CrearPagoRequest crear = new CrearPagoRequest(1L, new BigDecimal("750"), "PAG-REF", "TRANSFERENCIA");
        pagoApplicationService.crearPago(crear);

        // Act
        PagoResponse response = pagoApplicationService.obtenerPorReferencia("PAG-REF");

        // Assert
        assertNotNull(response);
        assertEquals("PAG-REF", response.referenciaExterna());
    }

    @Test
    @DisplayName("Listar pagos por obligacion")
    void testListarPagosPorObligacion() {
        // Arrange
        CrearPagoRequest pago1 = new CrearPagoRequest(5L, new BigDecimal("100"), "PAG-OBL-1", "PSE");
        CrearPagoRequest pago2 = new CrearPagoRequest(5L, new BigDecimal("200"), "PAG-OBL-2", "TARJETA");
        pagoApplicationService.crearPago(pago1);
        pagoApplicationService.crearPago(pago2);

        // Act
        List<PagoResponse> pagos = pagoApplicationService.listarPorObligacion(5L);

        // Assert
        assertEquals(2, pagos.size());
    }

    @Test
    @DisplayName("Confirmar pago exitoso")
    void testConfirmarPagoExitoso() {
        // Arrange
        CrearPagoRequest crear = new CrearPagoRequest(1L, new BigDecimal("500"), "PAG-CONFIRM", "PSE");
        PagoResponse creado = pagoApplicationService.crearPago(crear);
        assertEquals("PENDIENTE", creado.estado());

        ConfirmarPagoRequest confirmar = new ConfirmarPagoRequest("PAG-CONFIRM");

        // Act
        PagoResponse confirmado = pagoApplicationService.confirmarPago(confirmar);

        // Assert
        assertEquals("CONFIRMADO", confirmado.estado());
        assertNotNull(confirmado.confirmadoAt());
    }

    @Test
    @DisplayName("Confirmar pago no pendiente lanza excepcion")
    void testConfirmarPagoNoConfirmable() {
        // Arrange
        CrearPagoRequest crear = new CrearPagoRequest(1L, new BigDecimal("500"), "PAG-CONF-2", "TARJETA");
        pagoApplicationService.crearPago(crear);
        pagoApplicationService.confirmarPago(new ConfirmarPagoRequest("PAG-CONF-2"));

        // Act & Assert
        assertThrows(PagoBusinessException.class, () -> {
            pagoApplicationService.confirmarPago(new ConfirmarPagoRequest("PAG-CONF-2"));
        });
    }

    @Test
    @DisplayName("Rechazar pago exitoso")
    void testRechazarPagoExitoso() {
        // Arrange
        CrearPagoRequest crear = new CrearPagoRequest(1L, new BigDecimal("300"), "PAG-REJECT", "OFICINA");
        PagoResponse creado = pagoApplicationService.crearPago(crear);

        // Act
        PagoResponse rechazado = pagoApplicationService.rechazarPago(creado.id());

        // Assert
        assertEquals("RECHAZADO", rechazado.estado());
    }

    @Test
    @DisplayName("Flujo completo: Crear, confirmar pago")
    void testFlujoCompletoPago() {
        // Act 1: Crear
        CrearPagoRequest crear = new CrearPagoRequest(3L, new BigDecimal("2500.50"), "PAG-FLUJO", "TRANSFERENCIA");
        PagoResponse creado = pagoApplicationService.crearPago(crear);

        // Assert 1
        assertNotNull(creado.id());
        assertEquals("PENDIENTE", creado.estado());

        // Act 2: Obtener
        PagoResponse obtenido = pagoApplicationService.obtenerPorId(creado.id());
        assertEquals(creado.id(), obtenido.id());

        // Act 3: Confirmar
        PagoResponse confirmado = pagoApplicationService.confirmarPago(new ConfirmarPagoRequest("PAG-FLUJO"));

        // Assert 3
        assertEquals("CONFIRMADO", confirmado.estado());
        assertNotNull(confirmado.confirmadoAt());
    }
}

