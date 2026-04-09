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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
    @DisplayName("Register audit event successfully")
    void registrarExitoso() {
        RegisterAuditRequest request = new RegisterAuditRequest(
                "COLLECTION", "PAYMENT", 10L, "CONFIRMED", "system", "SYSTEM", "API", "corr-1", "detail"
        );

        assertDoesNotThrow(() -> service.registrar(request));
    }

    @Test
    @DisplayName("Register event wraps exception into AuditBusinessException")
    void registrarConError() {
        RegisterAuditRequest request = new RegisterAuditRequest(
                "COLLECTION", "PAYMENT", 10L, "CONFIRMED", "system", "SYSTEM", "API", "corr-1", "detail"
        );

        doThrow(new RuntimeException("db down"))
                .when(auditoriaService)
                .registerEvent(any(), any(), any(), any(), any(), any(), any(), any(), any());

        assertThrows(AuditBusinessException.class, () -> service.registrar(request));
    }

    @Test
    @DisplayName("List by entity returns ordered events")
    void listarPorEntidad() {
        AuditEvent a = AuditEvent.reconstruct(
                1L, "PAYMENT", 10L, "CREATED", "user", "ADMIN", "API", "COLLECTION", "corr-1", "d1", LocalDateTime.now()
        );
        AuditEvent b = AuditEvent.reconstruct(
                2L, "PAYMENT", 10L, "CONFIRMED", "user", "ADMIN", "API", "COLLECTION", "corr-2", "d2", LocalDateTime.now()
        );

        when(repository.findByEntidadAndEntidadIdOrderByCreatedAtDesc("PAYMENT", 10L)).thenReturn(List.of(b, a));

        List<AuditEventResponse> result = service.listarPorEntidad("PAYMENT", 10L);

        assertEquals(2, result.size());
        assertEquals("CONFIRMED", result.get(0).action());
    }
}
