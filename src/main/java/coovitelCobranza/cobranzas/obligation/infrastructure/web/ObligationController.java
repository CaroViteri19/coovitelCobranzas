package coovitelCobranza.cobranzas.obligation.infrastructure.web;

import coovitelCobranza.cobranzas.obligation.application.dto.ApplyObligationPaymentRequest;
import coovitelCobranza.cobranzas.obligation.application.dto.GetObligationByIdRequest;
import coovitelCobranza.cobranzas.obligation.application.dto.GetObligationByNumberRequest;
import coovitelCobranza.cobranzas.obligation.application.dto.ListObligationsByClientRequest;
import coovitelCobranza.cobranzas.obligation.application.dto.ObligationResponse;
import coovitelCobranza.cobranzas.obligation.application.dto.RegisterDelinquencyRequest;
import coovitelCobranza.cobranzas.obligation.application.service.ObligationApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/obligations")
@Tag(name = "Obligations", description = "Debt obligations management")
public class ObligationController {

    private final ObligationApplicationService obligationApplicationService;

    public ObligationController(ObligationApplicationService obligationApplicationService) {
        this.obligationApplicationService = obligationApplicationService;
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR','AUDITOR')")
    @PostMapping("/search/id")
    @Operation(summary = "Get obligation by id")
    public ResponseEntity<ObligationResponse> getById(@RequestBody GetObligationByIdRequest request) {
        return ResponseEntity.ok(obligationApplicationService.getById(request.obligationId()));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR','AUDITOR')")
    @PostMapping("/search/number")
    @Operation(summary = "Get obligation by number")
    public ResponseEntity<ObligationResponse> getByNumber(@RequestBody GetObligationByNumberRequest request) {
        return ResponseEntity.ok(obligationApplicationService.getByNumber(request.obligationNumber()));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR','AUDITOR')")
    @PostMapping("/search/client")
    @Operation(summary = "List obligations by client")
    public ResponseEntity<List<ObligationResponse>> listByClient(@RequestBody ListObligationsByClientRequest request) {
        return ResponseEntity.ok(obligationApplicationService.listByCustomer(request.clientId()));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR')")
    @PostMapping("/update/delinquency")
    @Operation(summary = "Register or update obligation delinquency")
    public ResponseEntity<ObligationResponse> registerDelinquency(@Valid @RequestBody RegisterDelinquencyRequest request) {
        return ResponseEntity.ok(obligationApplicationService.registerDelinquency(
                request.obligationId(),
                request.delinquencyDays(),
                request.overdueBalance()
        ));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR')")
    @PostMapping("/apply-payment")
    @Operation(summary = "Apply payment to obligation")
    public ResponseEntity<ObligationResponse> applyPayment(@Valid @RequestBody ApplyObligationPaymentRequest request) {
        return ResponseEntity.ok(obligationApplicationService.applyPayment(request.obligationId(), request.paymentAmount()));
    }
}

