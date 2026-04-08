package coovitelCobranza.cobranzas.auditoria.application.service;

import coovitelCobranza.cobranzas.auditoria.application.dto.AuditEventResponse;
import coovitelCobranza.cobranzas.auditoria.application.dto.RegisterAuditRequest;
import coovitelCobranza.cobranzas.auditoria.application.exception.AuditBusinessException;
import coovitelCobranza.cobranzas.auditoria.domain.model.AuditEvent;
import coovitelCobranza.cobranzas.auditoria.domain.repository.AuditEventRepository;
import coovitelCobranza.cobranzas.auditoria.domain.service.AuditService;
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
@DisplayName("Unit Tests - AuditApplicationService")
class AuditApplicationServiceTest {

    @Mock
    private AuditService auditoriaService;

    @Mock
    private AuditEventRepository repository;

    private AuditApplicationService service;

    @BeforeEach
    void setUp() {
        service = new AuditApplicationService(auditoriaService, repository);
    }

    @Test
    @DisplayName("Register evento de auditoría exitoso")
    void registrarExitoso() {
        RegisterAuditRequest request = new RegisterAuditRequest(
                "PAGO", 10L, "CONFIRMADO", "sistema", "detail"
        );

        assertDoesNotThrow(() -> service.registrar(request));
    }

    @Test
    @DisplayName("Register evento transforma excepción a AuditBusinessException")
    void registrarConError() {
        RegisterAuditRequest request = new RegisterAuditRequest(
                "PAGO", 10L, "CONFIRMADO", "sistema", "detail"
        );

        doThrow(new RuntimeException("db down"))
                .when(auditoriaService)
                .registrarEvent(anyString(), anyLong(), anyString(), anyString(), anyString());

        assertThrows(AuditBusinessException.class, () -> service.registrar(request));
    }

    @Test
    @DisplayName("Listar por entity retorna eventos ordenados")
    void listarPorEntidad() {
        AuditEvent a = AuditEvent.reconstruct(
                1L, "PAGO", 10L, "CREADO", "user", "d1", LocalDateTime.now()
        );
        AuditEvent b = AuditEvent.reconstruct(
                2L, "PAGO", 10L, "CONFIRMADO", "user", "d2", LocalDateTime.now()
        );

        when(repository.findByEntidadAndEntidadIdOrderByCreatedAtDesc("PAGO", 10L)).thenReturn(List.of(b, a));

        List<AuditEventResponse> result = service.listarPorEntidad("PAGO", 10L);

        assertEquals(2, result.size());
        assertEquals("CONFIRMADO", result.get(0).action());
    }
}

