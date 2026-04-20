package coovitelCobranza.cobranzas.orchestration.application.service;

import coovitelCobranza.cobranzas.casemanagement.domain.model.Case;
import coovitelCobranza.cobranzas.casemanagement.domain.repository.CaseRepository;
import coovitelCobranza.cobranzas.client.domain.model.Client;
import coovitelCobranza.cobranzas.client.domain.repository.ClientRepository;
import coovitelCobranza.cobranzas.interaction.domain.model.Interaction;
import coovitelCobranza.cobranzas.interaction.domain.repository.InteractionRepository;
import coovitelCobranza.cobranzas.obligation.domain.model.Obligation;
import coovitelCobranza.cobranzas.obligation.domain.repository.ObligationRepository;
import coovitelCobranza.cobranzas.orchestration.application.dto.SendPaymentLinkRequest;
import coovitelCobranza.cobranzas.orchestration.application.dto.SendPaymentLinkResponse;
import coovitelCobranza.cobranzas.orchestration.application.exception.CaseForOrchestrationNotFoundException;
import coovitelCobranza.cobranzas.orchestration.domain.service.ChannelOrchestrator;
import coovitelCobranza.cobranzas.orchestration.domain.template.TemplateRenderer;
import coovitelCobranza.cobranzas.payment.application.dto.GenerateLinkRequest;
import coovitelCobranza.cobranzas.payment.application.dto.GenerateLinkResponse;
import coovitelCobranza.cobranzas.payment.application.service.PaymentApplicationService;
import coovitelCobranza.cobranzas.payment.domain.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests - PaymentOrchestrationApplicationService")
class PaymentOrchestrationApplicationServiceTest {

    @Mock private CaseRepository caseRepository;
    @Mock private ObligationRepository obligationRepository;
    @Mock private ClientRepository clientRepository;
    @Mock private InteractionRepository interactionRepository;
    @Mock private PaymentApplicationService paymentApplicationService;
    @Mock private ChannelOrchestrator channelOrchestrator;

    // Renderer es puro; no mockeamos: usamos la implementación real para que
    // también quede cubierta la sustitución de placeholders.
    private final TemplateRenderer templateRenderer = new TemplateRenderer();

    private PaymentOrchestrationApplicationService service;

    private static final Long CASE_ID = 77L;
    private static final Long OBLIGATION_ID = 42L;
    private static final Long CUSTOMER_ID = 13L;

    @BeforeEach
    void setUp() {
        service = new PaymentOrchestrationApplicationService(
                caseRepository,
                obligationRepository,
                clientRepository,
                interactionRepository,
                paymentApplicationService,
                channelOrchestrator,
                templateRenderer
        );
    }

    // ----------------------------------------------------------------------
    // helpers
    // ----------------------------------------------------------------------

    private Case caseOpen() {
        return Case.reconstruct(
                CASE_ID, OBLIGATION_ID, Case.Priority.HIGH, Case.Status.IN_MANAGEMENT,
                "asesor1", null, LocalDateTime.now()
        );
    }

    private Obligation obligation() {
        return Obligation.reconstruct(
                OBLIGATION_ID, CUSTOMER_ID, "OB-001",
                new BigDecimal("150000"), new BigDecimal("50000"), 15,
                Obligation.StatusObligation.EN_MORA, LocalDate.now().plusDays(30), LocalDateTime.now()
        );
    }

    private Client clientWithConsents(boolean sms, boolean email, boolean whatsApp,
                                      String telefono, String emailAddr) {
        return Client.reconstructFull(
                CUSTOMER_ID, "CC", "1000", "Juan Pérez",
                telefono, emailAddr, null, "Bogotá", "SMS",
                whatsApp, sms, email, LocalDateTime.now()
        );
    }

    private GenerateLinkResponse fakeLink() {
        return new GenerateLinkResponse(
                999L,
                "https://pasarela.test/checkout/abc123",
                "SESSION-XYZ",
                LocalDateTime.now().plusHours(24),
                "GW-REF-001"
        );
    }

    /** Mock: orchestrator entrega como DELIVERED y echamos back un Interaction con id. */
    private void stubOrchestratorDelivered() {
        when(channelOrchestrator.send(anyLong(), any(), anyString(), anyString()))
                .thenAnswer(inv -> {
                    Long caseId = inv.getArgument(0);
                    Interaction.Channel channel = inv.getArgument(1);
                    String tpl = inv.getArgument(2);
                    Interaction i = Interaction.create(caseId, channel, tpl);
                    i.markDelivered();
                    return i;
                });
    }

    private void stubInteractionPersistenceAssigningId() {
        when(interactionRepository.save(any(Interaction.class))).thenAnswer(inv -> {
            Interaction in = inv.getArgument(0);
            // Reconstruimos con id arbitrario para simular persistencia.
            return Interaction.reconstruct(
                    1000L + in.getChannel().ordinal(),
                    in.getCaseId(), in.getChannel(), in.getTemplate(),
                    in.getResultStatus(), in.getCreatedAt()
            );
        });
    }

    // ----------------------------------------------------------------------
    // tests
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Happy path: 2 canales consentidos (SMS+EMAIL) — ambos entregados")
    void sendByConsents_TwoChannelsDelivered() {
        when(caseRepository.findById(CASE_ID)).thenReturn(Optional.of(caseOpen()));
        when(obligationRepository.findById(OBLIGATION_ID)).thenReturn(Optional.of(obligation()));
        when(clientRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(
                clientWithConsents(true, true, false, "3001112222", "juan@test.co")
        ));
        when(paymentApplicationService.generateLink(any(GenerateLinkRequest.class)))
                .thenReturn(fakeLink());
        stubOrchestratorDelivered();
        stubInteractionPersistenceAssigningId();

        SendPaymentLinkResponse response = service.generateAndSendLinkForCase(
                CASE_ID, SendPaymentLinkRequest.defaults()
        );

        assertEquals(CASE_ID, response.caseId());
        assertEquals(OBLIGATION_ID, response.obligationId());
        assertEquals(999L, response.paymentId());
        assertEquals("https://pasarela.test/checkout/abc123", response.paymentUrl());
        assertEquals(2, response.channelsAttempted());
        assertEquals(2, response.channelsDelivered());

        ArgumentCaptor<GenerateLinkRequest> linkCaptor = ArgumentCaptor.forClass(GenerateLinkRequest.class);
        verify(paymentApplicationService).generateLink(linkCaptor.capture());
        assertEquals(OBLIGATION_ID, linkCaptor.getValue().obligationId());
        assertEquals(Payment.PaymentMethod.PSE, linkCaptor.getValue().method());

        verify(channelOrchestrator).send(eq(CASE_ID), eq(Interaction.Channel.SMS), anyString(), eq("3001112222"));
        verify(channelOrchestrator).send(eq(CASE_ID), eq(Interaction.Channel.EMAIL), anyString(), eq("juan@test.co"));
        verify(channelOrchestrator, never()).send(eq(CASE_ID), eq(Interaction.Channel.WHATSAPP), anyString(), anyString());
        verify(interactionRepository, times(2)).save(any(Interaction.class));
    }

    @Test
    @DisplayName("Canales explícitos del request anulan los consents del cliente")
    void explicitChannelsOverrideConsents() {
        when(caseRepository.findById(CASE_ID)).thenReturn(Optional.of(caseOpen()));
        when(obligationRepository.findById(OBLIGATION_ID)).thenReturn(Optional.of(obligation()));
        when(clientRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(
                clientWithConsents(true, true, true, "3001112222", "juan@test.co")
        ));
        when(paymentApplicationService.generateLink(any(GenerateLinkRequest.class)))
                .thenReturn(fakeLink());
        stubOrchestratorDelivered();
        stubInteractionPersistenceAssigningId();

        Set<Interaction.Channel> only = EnumSet.of(Interaction.Channel.WHATSAPP);
        SendPaymentLinkResponse response = service.generateAndSendLinkForCase(
                CASE_ID,
                new SendPaymentLinkRequest(null, Payment.PaymentMethod.CARD, only)
        );

        assertEquals(1, response.channelsAttempted());
        assertEquals(1, response.channelsDelivered());
        verify(channelOrchestrator, times(1)).send(anyLong(), eq(Interaction.Channel.WHATSAPP), anyString(), anyString());
        verify(channelOrchestrator, never()).send(anyLong(), eq(Interaction.Channel.SMS), anyString(), anyString());
        verify(channelOrchestrator, never()).send(anyLong(), eq(Interaction.Channel.EMAIL), anyString(), anyString());

        ArgumentCaptor<GenerateLinkRequest> linkCaptor = ArgumentCaptor.forClass(GenerateLinkRequest.class);
        verify(paymentApplicationService).generateLink(linkCaptor.capture());
        assertEquals(Payment.PaymentMethod.CARD, linkCaptor.getValue().method());
    }

    @Test
    @DisplayName("VOICE se filtra defensivamente aunque venga en canales explícitos")
    void voiceIsAlwaysFilteredOut() {
        when(caseRepository.findById(CASE_ID)).thenReturn(Optional.of(caseOpen()));
        when(obligationRepository.findById(OBLIGATION_ID)).thenReturn(Optional.of(obligation()));
        when(clientRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(
                clientWithConsents(false, false, false, "3001112222", "juan@test.co")
        ));
        when(paymentApplicationService.generateLink(any(GenerateLinkRequest.class)))
                .thenReturn(fakeLink());

        Set<Interaction.Channel> onlyVoice = EnumSet.of(Interaction.Channel.VOICE);
        SendPaymentLinkResponse response = service.generateAndSendLinkForCase(
                CASE_ID, new SendPaymentLinkRequest(null, null, onlyVoice)
        );

        assertEquals(0, response.channelsAttempted());
        assertEquals(0, response.channelsDelivered());
        verify(channelOrchestrator, never()).send(anyLong(), any(), anyString(), anyString());
        verify(interactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Cliente sin consents y sin canales explícitos → 0 envíos pero link creado")
    void noConsents_returnsPaymentIdWithZeroAttempts() {
        when(caseRepository.findById(CASE_ID)).thenReturn(Optional.of(caseOpen()));
        when(obligationRepository.findById(OBLIGATION_ID)).thenReturn(Optional.of(obligation()));
        when(clientRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(
                clientWithConsents(false, false, false, "3001112222", "juan@test.co")
        ));
        when(paymentApplicationService.generateLink(any(GenerateLinkRequest.class)))
                .thenReturn(fakeLink());

        SendPaymentLinkResponse response = service.generateAndSendLinkForCase(
                CASE_ID, SendPaymentLinkRequest.defaults()
        );

        assertEquals(999L, response.paymentId());
        assertEquals(0, response.channelsAttempted());
        assertEquals(0, response.channelsDelivered());
        assertTrue(response.dispatches().isEmpty());
        verify(channelOrchestrator, never()).send(anyLong(), any(), anyString(), anyString());
        verify(interactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Canal consentido pero destino vacío → se salta y no se persiste Interaction")
    void consentedChannelWithMissingDestinationIsSkipped() {
        when(caseRepository.findById(CASE_ID)).thenReturn(Optional.of(caseOpen()));
        when(obligationRepository.findById(OBLIGATION_ID)).thenReturn(Optional.of(obligation()));
        // email consentido pero el email del cliente es null
        when(clientRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(
                clientWithConsents(true, true, false, "3001112222", null)
        ));
        when(paymentApplicationService.generateLink(any(GenerateLinkRequest.class)))
                .thenReturn(fakeLink());
        stubOrchestratorDelivered();
        stubInteractionPersistenceAssigningId();

        SendPaymentLinkResponse response = service.generateAndSendLinkForCase(
                CASE_ID, SendPaymentLinkRequest.defaults()
        );

        // Sólo SMS entrega; EMAIL se salta por falta de destino.
        assertEquals(1, response.channelsAttempted());
        assertEquals(1, response.channelsDelivered());
        assertEquals(2, response.dispatches().size()); // SMS delivered + EMAIL skipped

        boolean emailSkipped = response.dispatches().stream()
                .anyMatch(d -> d.channel() == Interaction.Channel.EMAIL && !d.delivered() && d.destination() == null);
        assertTrue(emailSkipped, "EMAIL debería aparecer como skipped");

        verify(channelOrchestrator).send(eq(CASE_ID), eq(Interaction.Channel.SMS), anyString(), eq("3001112222"));
        verify(channelOrchestrator, never()).send(anyLong(), eq(Interaction.Channel.EMAIL), anyString(), anyString());
    }

    @Test
    @DisplayName("Fallo del proveedor en un canal no tumba los otros; se registra FAILED")
    void providerFailureIsRecordedWithoutBreakingOtherChannels() {
        when(caseRepository.findById(CASE_ID)).thenReturn(Optional.of(caseOpen()));
        when(obligationRepository.findById(OBLIGATION_ID)).thenReturn(Optional.of(obligation()));
        when(clientRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(
                clientWithConsents(true, true, false, "3001112222", "juan@test.co")
        ));
        when(paymentApplicationService.generateLink(any(GenerateLinkRequest.class)))
                .thenReturn(fakeLink());

        when(channelOrchestrator.send(anyLong(), any(), anyString(), anyString()))
                .thenAnswer(inv -> {
                    Interaction.Channel channel = inv.getArgument(1);
                    Interaction i = Interaction.create(
                            inv.getArgument(0), channel, inv.getArgument(2));
                    if (channel == Interaction.Channel.SMS) {
                        i.markFailed();
                    } else {
                        i.markDelivered();
                    }
                    return i;
                });
        stubInteractionPersistenceAssigningId();

        SendPaymentLinkResponse response = service.generateAndSendLinkForCase(
                CASE_ID, SendPaymentLinkRequest.defaults()
        );

        assertEquals(2, response.channelsAttempted());
        assertEquals(1, response.channelsDelivered(), "sólo EMAIL se entregó");

        boolean smsFailed = response.dispatches().stream()
                .anyMatch(d -> d.channel() == Interaction.Channel.SMS && !d.delivered());
        boolean emailDelivered = response.dispatches().stream()
                .anyMatch(d -> d.channel() == Interaction.Channel.EMAIL && d.delivered());
        assertTrue(smsFailed);
        assertTrue(emailDelivered);
        verify(interactionRepository, times(2)).save(any(Interaction.class));
    }

    @Test
    @DisplayName("Caso inexistente → CaseForOrchestrationNotFoundException")
    void caseNotFound() {
        when(caseRepository.findById(CASE_ID)).thenReturn(Optional.empty());

        assertThrows(CaseForOrchestrationNotFoundException.class,
                () -> service.generateAndSendLinkForCase(CASE_ID, SendPaymentLinkRequest.defaults()));

        verify(paymentApplicationService, never()).generateLink(any());
        verify(channelOrchestrator, never()).send(anyLong(), any(), anyString(), anyString());
    }

    @Test
    @DisplayName("Body del SMS contiene los placeholders renderizados (fullName, obligación y url)")
    void renderedBodyContainsExpectedSubstitutions() {
        when(caseRepository.findById(CASE_ID)).thenReturn(Optional.of(caseOpen()));
        when(obligationRepository.findById(OBLIGATION_ID)).thenReturn(Optional.of(obligation()));
        when(clientRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(
                clientWithConsents(true, false, false, "3001112222", null)
        ));
        when(paymentApplicationService.generateLink(any(GenerateLinkRequest.class)))
                .thenReturn(fakeLink());
        stubOrchestratorDelivered();
        stubInteractionPersistenceAssigningId();

        service.generateAndSendLinkForCase(CASE_ID, SendPaymentLinkRequest.defaults());

        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(channelOrchestrator).send(eq(CASE_ID), eq(Interaction.Channel.SMS),
                bodyCaptor.capture(), eq("3001112222"));

        String body = bodyCaptor.getValue();
        assertNotNull(body);
        assertTrue(body.contains("Juan Pérez"), "debe incluir nombre");
        assertTrue(body.contains("OB-001"), "debe incluir número de obligación");
        assertTrue(body.contains("https://pasarela.test/checkout/abc123"), "debe incluir url de la pasarela");
        assertFalse(body.contains("{{"), "no debe quedar ningún placeholder sin resolver");
    }
}
