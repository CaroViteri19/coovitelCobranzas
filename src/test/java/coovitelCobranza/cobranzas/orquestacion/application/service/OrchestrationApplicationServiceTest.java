package coovitelCobranza.cobranzas.orquestacion.application.service;

import coovitelCobranza.cobranzas.interaction.domain.model.Interaction;
import coovitelCobranza.cobranzas.orquestacion.application.dto.OrchestrationExecutionResponse;
import coovitelCobranza.cobranzas.orquestacion.application.dto.SendOrchestrationRequest;
import coovitelCobranza.cobranzas.orquestacion.application.exception.OrchestrationBusinessException;
import coovitelCobranza.cobranzas.orquestacion.application.exception.OrchestrationNotFoundException;
import coovitelCobranza.cobranzas.orquestacion.domain.model.OrchestrationExecution;
import coovitelCobranza.cobranzas.orquestacion.domain.repository.OrchestrationExecutionRepository;
import coovitelCobranza.cobranzas.orquestacion.domain.service.OrquestadorCanales;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests - OrchestrationApplicationService")
class OrchestrationApplicationServiceTest {

    @Mock
    private OrchestrationExecutionRepository repository;

    @Mock
    private OrquestadorCanales orchestratorChannels;

    private OrchestrationApplicationService service;

    @BeforeEach
    void setUp() {
        service = new OrchestrationApplicationService(repository, orchestratorChannels);
    }

    @Test
    @DisplayName("Send orchestration stores execution as SENT")
    void sendSuccess() {
        SendOrchestrationRequest request = new SendOrchestrationRequest(10L, "SMS", "3001234567", "Template");

        when(orchestratorChannels.send(eq(10L), eq(Interaction.Channel.SMS), eq("Template"), eq("3001234567")))
                .thenReturn(Interaction.crear(10L, Interaction.Channel.SMS, "Template"));

        OrchestrationExecution saved = OrchestrationExecution.reconstruct(
                1L, 10L, "SMS", "3001234567", "Template", OrchestrationExecution.Status.ENVIADO,
                LocalDateTime.now()
        );
        when(repository.save(any(OrchestrationExecution.class))).thenReturn(saved);

        OrchestrationExecutionResponse response = service.send(request);

        assertEquals(1L, response.id());
        assertEquals("ENVIADO", response.estado());
    }

    @Test
    @DisplayName("Send fails with invalid channel")
    void sendInvalidChannel() {
        SendOrchestrationRequest request = new SendOrchestrationRequest(10L, "TELEGRAM", "x", "y");

        assertThrows(OrchestrationBusinessException.class, () -> service.send(request));
    }

    @Test
    @DisplayName("Get by id throws not found")
    void getByIdNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(OrchestrationNotFoundException.class, () -> service.getById(99L));
    }
}

