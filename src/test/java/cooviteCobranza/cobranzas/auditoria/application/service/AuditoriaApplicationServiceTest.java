package cooviteCobranza.cobranzas.auditoria.application.service;

import cooviteCobranza.cobranzas.auditoria.application.dto.AuditoriaEventoResponse;
import cooviteCobranza.cobranzas.auditoria.application.dto.RegistrarAuditoriaRequest;
import cooviteCobranza.cobranzas.auditoria.application.exception.AuditoriaBusinessException;
import cooviteCobranza.cobranzas.auditoria.domain.model.AuditoriaEvento;
import cooviteCobranza.cobranzas.auditoria.domain.repository.AuditoriaEventoRepository;
import cooviteCobranza.cobranzas.auditoria.domain.service.AuditoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests - AuditoriaApplicationService")
class AuditoriaApplicationServiceTest {

    @Mock
    private AuditoriaService auditoriaService;

    @Mock
    private AuditoriaEventoRepository repository;

    private AuditoriaApplicationService service;

    @BeforeEach
    void setUp() {
        service = new AuditoriaApplicationService(auditoriaService, repository);
    }

    @Test
    @DisplayName("Registrar evento de auditoría exitoso")
    void registrarExitoso() {
        RegistrarAuditoriaRequest request = new RegistrarAuditoriaRequest(
                "PAGO", 10L, "CONFIRMADO", "sistema", "detalle"
        );

        assertDoesNotThrow(() -> service.registrar(request));
    }

    @Test
    @DisplayName("Registrar evento transforma excepción a AuditoriaBusinessException")
    void registrarConError() {
        RegistrarAuditoriaRequest request = new RegistrarAuditoriaRequest(
                "PAGO", 10L, "CONFIRMADO", "sistema", "detalle"
        );

        doThrow(new RuntimeException("db down"))
                .when(auditoriaService)
                .registrarEvento(anyString(), anyLong(), anyString(), anyString(), anyString());

        assertThrows(AuditoriaBusinessException.class, () -> service.registrar(request));
    }

    @Test
    @DisplayName("Listar por entidad retorna eventos ordenados")
    void listarPorEntidad() {
        AuditoriaEvento a = AuditoriaEvento.reconstruir(
                1L, "PAGO", 10L, "CREADO", "user", "d1", LocalDateTime.now()
        );
        AuditoriaEvento b = AuditoriaEvento.reconstruir(
                2L, "PAGO", 10L, "CONFIRMADO", "user", "d2", LocalDateTime.now()
        );

        when(repository.findByEntidadAndEntidadIdOrderByCreatedAtDesc("PAGO", 10L)).thenReturn(List.of(b, a));

        List<AuditoriaEventoResponse> result = service.listarPorEntidad("PAGO", 10L);

        assertEquals(2, result.size());
        assertEquals("CONFIRMADO", result.get(0).accion());
    }
}

