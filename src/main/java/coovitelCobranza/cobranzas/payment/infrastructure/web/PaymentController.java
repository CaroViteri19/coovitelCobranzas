package coovitelCobranza.cobranzas.payment.infrastructure.web;

import coovitelCobranza.cobranzas.payment.application.dto.ConfirmPaymentRequest;
import coovitelCobranza.cobranzas.payment.application.dto.CreatePaymentRequest;
import coovitelCobranza.cobranzas.payment.application.dto.GetPaymentByIdRequest;
import coovitelCobranza.cobranzas.payment.application.dto.GetPaymentByReferenceRequest;
import coovitelCobranza.cobranzas.payment.application.dto.ListPaymentsByObligationRequest;
import coovitelCobranza.cobranzas.payment.application.dto.PaymentResponse;
import coovitelCobranza.cobranzas.payment.application.dto.RejectPaymentRequest;
import coovitelCobranza.cobranzas.payment.application.service.PaymentApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PaymentController {

    private final PaymentApplicationService paymentApplicationService;

    public PaymentController(PaymentApplicationService paymentApplicationService) {
        this.paymentApplicationService = paymentApplicationService;
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR','AGENTE')")
    @PostMapping
    public ResponseEntity<PaymentResponse> create(@RequestBody CreatePaymentRequest request) {
        PaymentResponse response = paymentApplicationService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR','AGENTE','AUDITOR')")
    @PostMapping("/search/id")
    public ResponseEntity<PaymentResponse> getById(@RequestBody GetPaymentByIdRequest request) {
        PaymentResponse response = paymentApplicationService.getById(request.paymentId());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR','AGENTE','AUDITOR')")
    @PostMapping("/search/reference")
    public ResponseEntity<PaymentResponse> getByReference(@RequestBody GetPaymentByReferenceRequest request) {
        PaymentResponse response = paymentApplicationService.getByReference(request.externalReference());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR','AGENTE','AUDITOR')")
    @PostMapping("/search/obligation")
    public ResponseEntity<List<PaymentResponse>> listByObligation(@RequestBody ListPaymentsByObligationRequest request) {
        List<PaymentResponse> response = paymentApplicationService.listByObligation(request.obligationId());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR')")
    @PostMapping("/confirm")
    public ResponseEntity<PaymentResponse> confirmPayment(@RequestBody ConfirmPaymentRequest request) {
        PaymentResponse response = paymentApplicationService.confirmPayment(request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR')")
    @PostMapping("/reject")
    public ResponseEntity<PaymentResponse> rejectPayment(@RequestBody RejectPaymentRequest request) {
        PaymentResponse response = paymentApplicationService.rejectPayment(request.paymentId());
        return ResponseEntity.ok(response);
    }
}

