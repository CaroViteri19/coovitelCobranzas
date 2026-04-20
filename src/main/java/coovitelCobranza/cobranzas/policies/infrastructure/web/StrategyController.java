package coovitelCobranza.cobranzas.policies.infrastructure.web;

import coovitelCobranza.cobranzas.policies.application.dto.CreateStrategyRequest;
import coovitelCobranza.cobranzas.policies.application.dto.StrategyResponse;
import coovitelCobranza.cobranzas.policies.application.service.StrategyApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Strategy REST Controller
 *
 * M3 Políticas – permission matrix:
 *   ADMINISTRADOR : full  (read + write)
 *   SUPERVISOR    : read  (read only)
 *   AGENTE        : none
 *   AUDITOR       : read  (read only)
 */
@RestController
@RequestMapping("/api/v1/strategies")
@CrossOrigin(origins = "*", maxAge = 3600)
public class StrategyController {

    private final StrategyApplicationService service;

    public StrategyController(StrategyApplicationService service) {
        this.service = service;
    }

    /**
     * Create a new collection strategy.
     * Write operation – ADMINISTRADOR only.
     */
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping
    public ResponseEntity<StrategyResponse> create(@RequestBody CreateStrategyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    /**
     * Get a strategy by its ID.
     * Read operation – ADMINISTRADOR, SUPERVISOR, AUDITOR.
     */
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR','AUDITOR')")
    @PostMapping("/search/id")
    public ResponseEntity<StrategyResponse> getById(@RequestBody java.util.Map<String, Long> body) {
        return ResponseEntity.ok(service.getById(body.get("strategyId")));
    }

    /**
     * List all active strategies.
     * Read operation – ADMINISTRADOR, SUPERVISOR, AUDITOR.
     */
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR','AUDITOR')")
    @GetMapping("/active")
    public ResponseEntity<List<StrategyResponse>> listActive() {
        return ResponseEntity.ok(service.listActive());
    }

    /**
     * Activate a strategy.
     * Write operation – ADMINISTRADOR only.
     */
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/activate")
    public ResponseEntity<StrategyResponse> activate(@RequestBody java.util.Map<String, Long> body) {
        return ResponseEntity.ok(service.activate(body.get("strategyId")));
    }

    /**
     * Deactivate a strategy.
     * Write operation – ADMINISTRADOR only.
     */
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/deactivate")
    public ResponseEntity<StrategyResponse> deactivate(@RequestBody java.util.Map<String, Long> body) {
        return ResponseEntity.ok(service.deactivate(body.get("strategyId")));
    }
}
