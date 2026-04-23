package coovitelCobranza.cobranzas.audit.infrastructure.web;

import coovitelCobranza.cobranzas.audit.application.dto.AuditEventResponse;
import coovitelCobranza.cobranzas.audit.application.dto.ListAuditEventsByEntityRequest;
import coovitelCobranza.cobranzas.audit.application.dto.RegisterAuditRequest;
import coovitelCobranza.cobranzas.audit.application.service.AuditApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Audit REST Controller
 * 
 * Security: All sensitive data (entity IDs) is received in request body, not in path.
 * This ensures data is encrypted in transit via HTTPS.
 */
@RestController
@RequestMapping("/api/v1/audit")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuditController {

    private final AuditApplicationService service;

    public AuditController(AuditApplicationService service) {
        this.service = service;
    }

    /**
     * Register an audit event.
     * 
     * @param request the audit event to register
     * @return 201 Created
     */
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','SUPERVISOR')")
    @PostMapping("/events")
    public ResponseEntity<Void> registerEvent(@RequestBody RegisterAuditRequest request) {
        service.registerEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * List audit events for an entity.
     * 
     * @param request contains entityType and entityId in encrypted body
     * @return list of audit events
     */
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','SUPERVISOR','AGENTE','AUDITOR')")
    @PostMapping("/events/search")
    public ResponseEntity<List<AuditEventResponse>> listEventsByEntity(@RequestBody ListAuditEventsByEntityRequest request) {
        return ResponseEntity.ok(service.listEventsByEntity(request.entityType(), request.entityId()));
    }
}

