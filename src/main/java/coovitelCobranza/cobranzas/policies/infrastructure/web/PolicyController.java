package coovitelCobranza.cobranzas.policies.infrastructure.web;

import coovitelCobranza.cobranzas.policies.application.dto.CreatePolicyRequest;
import coovitelCobranza.cobranzas.policies.application.dto.PolicyResponse;
import coovitelCobranza.cobranzas.policies.application.service.PolicyApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Policy REST Controller
 *
 * M3 Políticas – permission matrix:
 *   ADMINISTRADOR : full  (read + write)
 *   SUPERVISOR    : read  (read only)
 *   AGENTE        : none
 *   AUDITOR       : read  (read only)
 */
@RestController
@RequestMapping("/api/v1/policies")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PolicyController {

    private final PolicyApplicationService service;

    public PolicyController(PolicyApplicationService service) {
        this.service = service;
    }

    /**
     * Create a new collection policy.
     * Write operation – ADMINISTRADOR only.
     */
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping
    public ResponseEntity<PolicyResponse> create(@RequestBody CreatePolicyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    /**
     * Get a policy by its ID.
     * Read operation – ADMINISTRADOR, SUPERVISOR, AUDITOR.
     */
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR','AUDITOR')")
    @PostMapping("/search/id")
    public ResponseEntity<PolicyResponse> getById(@RequestBody java.util.Map<String, Long> body) {
        return ResponseEntity.ok(service.getById(body.get("policyId")));
    }

    /**
     * List all policies for a given strategy.
     * Read operation – ADMINISTRADOR, SUPERVISOR, AUDITOR.
     */
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR','AUDITOR')")
    @PostMapping("/search/strategy")
    public ResponseEntity<List<PolicyResponse>> listByStrategy(@RequestBody java.util.Map<String, Long> body) {
        return ResponseEntity.ok(service.listByStrategy(body.get("strategyId")));
    }

    /**
     * List all active policies.
     * Read operation – ADMINISTRADOR, SUPERVISOR, AUDITOR.
     */
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR','AUDITOR')")
    @GetMapping("/active")
    public ResponseEntity<List<PolicyResponse>> listActive() {
        return ResponseEntity.ok(service.listActive());
    }

    /**
     * Activate a policy.
     * Write operation – ADMINISTRADOR only.
     */
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/activate")
    public ResponseEntity<PolicyResponse> activate(@RequestBody java.util.Map<String, Long> body) {
        return ResponseEntity.ok(service.activate(body.get("policyId")));
    }

    /**
     * Deactivate a policy.
     * Write operation – ADMINISTRADOR only.
     */
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/deactivate")
    public ResponseEntity<PolicyResponse> deactivate(@RequestBody java.util.Map<String, Long> body) {
        return ResponseEntity.ok(service.deactivate(body.get("policyId")));
    }
}
