package coovitelCobranza.cobranzas.orchestration.infrastructure.web;

import coovitelCobranza.cobranzas.orchestration.application.dto.SendOrchestrationRequest;
import coovitelCobranza.cobranzas.orchestration.application.dto.GetOrchestrationByIdRequest;
import coovitelCobranza.cobranzas.orchestration.application.dto.ListOrchestrationByCaseRequest;
import coovitelCobranza.cobranzas.orchestration.application.dto.OrchestrationExecutionResponse;
import coovitelCobranza.cobranzas.orchestration.application.service.OrchestrationApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orchestration")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrchestrationController {

    private final OrchestrationApplicationService service;

    public OrchestrationController(OrchestrationApplicationService service) {
        this.service = service;
    }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR','SUPERVISOR')")
    @PostMapping("/send")
    public ResponseEntity<OrchestrationExecutionResponse> send(@RequestBody SendOrchestrationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.send(request));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR','SUPERVISOR','AUDITOR')")
    @PostMapping("/search/id")
    public ResponseEntity<OrchestrationExecutionResponse> getById(@RequestBody GetOrchestrationByIdRequest request) {
        return ResponseEntity.ok(service.getById(request.executionId()));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR','SUPERVISOR','AUDITOR')")
    @PostMapping("/search/case")
    public ResponseEntity<List<OrchestrationExecutionResponse>> listByCase(@RequestBody ListOrchestrationByCaseRequest request) {
        return ResponseEntity.ok(service.listByCase(request.caseId()));
    }
}

