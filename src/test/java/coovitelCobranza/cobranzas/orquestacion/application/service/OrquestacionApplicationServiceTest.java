package coovitelCobranza.cobranzas.orquestacion.application.service;

import coovitelCobranza.cobranzas.interaccion.domain.model.Interaccion;
import coovitelCobranza.cobranzas.orquestacion.application.dto.EnviarOrquestacionRequest;
import coovitelCobranza.cobranzas.orquestacion.application.dto.OrquestacionEjecucionResponse;
import coovitelCobranza.cobranzas.orquestacion.application.exception.OrquestacionBusinessException;
import coovitelCobranza.cobranzas.orquestacion.application.exception.OrquestacionNotFoundException;
import coovitelCobranza.cobranzas.orquestacion.domain.model.OrquestacionEjecucion;
import coovitelCobranza.cobranzas.orquestacion.domain.repository.OrquestacionEjecucionRepository;
import coovitelCobranza.cobranzas.orquestacion.domain.service.OrquestadorCanales;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests - OrquestacionApplicationService")
class OrquestacionApplicationServiceTest {

    @Mock
    private OrquestacionEjecucionRepository repository;

    @Mock
    private OrquestadorCanales orquestadorCanales;

    private OrquestacionApplicationService service;

    @BeforeEach
    void setUp() {
        service = new OrquestacionApplicationService(repository, orquestadorCanales);
    }

    @Test
    @DisplayName("Enviar orquestación guarda ejecución en estado ENVIADO")
    void enviarExitoso() {
        EnviarOrquestacionRequest request = new EnviarOrquestacionRequest(10L, "SMS", "3001234567", "Plantilla");

        when(orquestadorCanales.enviar(eq(10L), eq(Interaccion.Canal.SMS), eq("Plantilla"), eq("3001234567")))
                .thenReturn(Interaccion.crear(10L, Interaccion.Canal.SMS, "Plantilla"));

        OrquestacionEjecucion saved = OrquestacionEjecucion.reconstruir(
                1L, 10L, "SMS", "3001234567", "Plantilla", OrquestacionEjecucion.Estado.ENVIADO,
                LocalDateTime.now()
        );
        when(repository.save(any(OrquestacionEjecucion.class))).thenReturn(saved);

        OrquestacionEjecucionResponse response = service.enviar(request);

        assertEquals(1L, response.id());
        assertEquals("ENVIADO", response.estado());
    }

    @Test
    @DisplayName("Enviar falla con canal inválido")
    void enviarCanalInvalido() {
        EnviarOrquestacionRequest request = new EnviarOrquestacionRequest(10L, "TELEGRAM", "x", "y");

        assertThrows(OrquestacionBusinessException.class, () -> service.enviar(request));
    }

    @Test
    @DisplayName("Obtener por id no encontrado")
    void obtenerNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(OrquestacionNotFoundException.class, () -> service.obtenerPorId(99L));
    }
}

