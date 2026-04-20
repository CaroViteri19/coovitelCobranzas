package coovitelCobranza.cobranzas.payment.application.service;

import coovitelCobranza.cobranzas.payment.application.dto.ConfirmPaymentRequest;
import coovitelCobranza.cobranzas.payment.application.dto.CreatePaymentRequest;
import coovitelCobranza.cobranzas.payment.application.dto.PaymentResponse;
import coovitelCobranza.cobranzas.payment.application.exception.PaymentBusinessException;
import coovitelCobranza.cobranzas.payment.application.exception.PaymentNotFoundException;
import coovitelCobranza.cobranzas.payment.domain.event.PaymentConfirmedEvent;
import coovitelCobranza.cobranzas.payment.domain.model.Payment;
import coovitelCobranza.cobranzas.payment.domain.repository.PaymentRepository;
import coovitelCobranza.cobranzas.shared.domain.event.DomainEvent;
import coovitelCobranza.cobranzas.shared.domain.event.DomainEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests - PaymentApplicationService")
class PaymentApplicationServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private DomainEventPublisher eventPublisher;

    private PaymentApplicationService service;

    @BeforeEach
    void setUp() {
        service = new PaymentApplicationService(paymentRepository, eventPublisher);
    }

    @Test
    @DisplayName("Create payment returns PENDING response when data is valid")
    void createPaymentSuccess() {
        CreatePaymentRequest request = new CreatePaymentRequest(
                10L,
                BigDecimal.valueOf(150000),
                "REF-100",
                "PSE"
        );

        when(paymentRepository.findByExternalReference("REF-100")).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment pending = invocation.getArgument(0);
            return Payment.reconstruct(
                    1L,
                    pending.getObligationId(),
                    pending.getAmount(),
                    pending.getExternalReference(),
                    pending.getMethod(),
                    pending.getStatus(),
                    pending.getConfirmedAt(),
                    pending.getCreatedAt()
            );
        });

        PaymentResponse response = service.createPayment(request);

        assertEquals(1L, response.id());
        assertEquals("PENDING", response.status());
        assertEquals("PSE", response.method());
    }

    @Test
    @DisplayName("Create payment fails when external reference already exists")
    void createPaymentDuplicateReference() {
        CreatePaymentRequest request = new CreatePaymentRequest(
                10L,
                BigDecimal.TEN,
                "REF-EXISTING",
                "PSE"
        );

        when(paymentRepository.findByExternalReference("REF-EXISTING")).thenReturn(
                Optional.of(Payment.reconstruct(
                        1L,
                        10L,
                        BigDecimal.TEN,
                        "REF-EXISTING",
                        Payment.PaymentMethod.PSE,
                        Payment.PaymentStatus.PENDING,
                        null,
                        LocalDateTime.now()
                ))
        );

        assertThrows(PaymentBusinessException.class, () -> service.createPayment(request));
    }

    @Test
    @DisplayName("Create payment fails when amount is zero or negative")
    void createPaymentInvalidAmount() {
        CreatePaymentRequest request = new CreatePaymentRequest(
                10L,
                BigDecimal.ZERO,
                "REF-101",
                "PSE"
        );

        when(paymentRepository.findByExternalReference("REF-101")).thenReturn(Optional.empty());

        assertThrows(PaymentBusinessException.class, () -> service.createPayment(request));
    }

    @Test
    @DisplayName("Create payment fails when method is invalid")
    void createPaymentInvalidMethod() {
        CreatePaymentRequest request = new CreatePaymentRequest(
                10L,
                BigDecimal.valueOf(30000),
                "REF-102",
                "NEQUI"
        );

        when(paymentRepository.findByExternalReference("REF-102")).thenReturn(Optional.empty());

        assertThrows(PaymentBusinessException.class, () -> service.createPayment(request));
    }

    @Test
    @DisplayName("Confirm payment changes status and publishes PaymentConfirmed event")
    void confirmPaymentSuccessPublishesEvent() {
        Payment pendingPayment = Payment.reconstruct(
                8L,
                20L,
                BigDecimal.valueOf(75000),
                "REF-200",
                Payment.PaymentMethod.CARD,
                Payment.PaymentStatus.PENDING,
                null,
                LocalDateTime.now()
        );

        when(paymentRepository.findByExternalReference("REF-200")).thenReturn(Optional.of(pendingPayment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = service.confirmPayment(new ConfirmPaymentRequest("REF-200"));

        assertEquals("CONFIRMED", response.status());
        assertNotNull(response.confirmedAt());

        ArgumentCaptor<DomainEvent> eventCaptor = ArgumentCaptor.forClass(DomainEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());

        PaymentConfirmedEvent event = (PaymentConfirmedEvent) eventCaptor.getValue();
        assertEquals(8L, event.paymentId());
        assertEquals(20L, event.obligationId());
        assertEquals(BigDecimal.valueOf(75000), event.amount());
        assertNotNull(event.occurredOn());
    }

    @Test
    @DisplayName("Confirm payment fails when payment is not in PENDING status")
    void confirmPaymentFailsIfAlreadyConfirmed() {
        Payment confirmedPayment = Payment.reconstruct(
                9L,
                30L,
                BigDecimal.valueOf(99000),
                "REF-201",
                Payment.PaymentMethod.TRANSFER,
                Payment.PaymentStatus.CONFIRMED,
                LocalDateTime.now(),
                LocalDateTime.now().minusDays(1)
        );

        when(paymentRepository.findByExternalReference("REF-201")).thenReturn(Optional.of(confirmedPayment));

        assertThrows(PaymentBusinessException.class,
                () -> service.confirmPayment(new ConfirmPaymentRequest("REF-201")));
    }

    @Test
    @DisplayName("Reject payment changes status to REJECTED")
    void rejectPaymentSuccess() {
        Payment pendingPayment = Payment.reconstruct(
                7L,
                15L,
                BigDecimal.valueOf(45000),
                "REF-300",
                Payment.PaymentMethod.OFFICE,
                Payment.PaymentStatus.PENDING,
                null,
                LocalDateTime.now()
        );

        when(paymentRepository.findById(7L)).thenReturn(Optional.of(pendingPayment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = service.rejectPayment(7L);

        assertEquals("REJECTED", response.status());
    }

    @Test
    @DisplayName("Get by id fails when payment does not exist")
    void getByIdNotFound() {
        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () -> service.getById(999L));
    }

    @Test
    @DisplayName("List by obligation returns mapped response list")
    void listByObligationSuccess() {
        Payment p1 = Payment.reconstruct(
                1L, 44L, BigDecimal.valueOf(1000), "REF-L1", Payment.PaymentMethod.PSE,
                Payment.PaymentStatus.PENDING, null, LocalDateTime.now()
        );
        Payment p2 = Payment.reconstruct(
                2L, 44L, BigDecimal.valueOf(2000), "REF-L2", Payment.PaymentMethod.CARD,
                Payment.PaymentStatus.CONFIRMED, LocalDateTime.now(), LocalDateTime.now().minusHours(2)
        );

        when(paymentRepository.findByObligationId(44L)).thenReturn(List.of(p1, p2));

        List<PaymentResponse> result = service.listByObligation(44L);

        assertEquals(2, result.size());
        assertEquals("REF-L1", result.get(0).externalReference());
        assertEquals("REF-L2", result.get(1).externalReference());
    }
}

