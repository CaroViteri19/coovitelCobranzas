package coovitelCobranza.cobranzas.scoring.infrastructure.web;

import coovitelCobranza.cobranzas.scoring.application.dto.CalculateScoringRequest;
import coovitelCobranza.cobranzas.scoring.application.dto.GetScoringByIdRequest;
import coovitelCobranza.cobranzas.scoring.application.dto.ListScoringByClientRequest;
import coovitelCobranza.cobranzas.scoring.application.dto.ListScoringByObligationRequest;
import coovitelCobranza.cobranzas.scoring.application.dto.ScoringSegmentationResponse;
import coovitelCobranza.cobranzas.scoring.application.service.ScoringSegmentationApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Scoring REST Controller
 * 
 * Security: All search parameters are in request body (encrypted), not in URL path.
 */
@RestController
@RequestMapping("/api/v1/scoring")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ScoringSegmentationController {

    private final ScoringSegmentationApplicationService service;

    public ScoringSegmentationController(ScoringSegmentationApplicationService service) {
        this.service = service;
    }

    /**
     * Calculate scoring and segmentation for an obligation.
     * 
     * @param request calculation parameters
     * @return 201 Created with scoring result
     */
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','SUPERVISOR')")
    @PostMapping("/calculate")
    public ResponseEntity<ScoringSegmentationResponse> calculate(@RequestBody CalculateScoringRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.calculate(request));
    }

    /**
     * Get scoring by ID.
     * 
     * @param request scoring lookup request
     * @return 200 OK with scoring data
     */
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','SUPERVISOR','AUDITOR')")
    @PostMapping("/search/id")
    public ResponseEntity<ScoringSegmentationResponse> getById(@RequestBody GetScoringByIdRequest request) {
        return ResponseEntity.ok(service.getById(request.scoringId()));
    }

    /**
     * Get latest scoring for an obligation.
     * Note: obligationId is encrypted in request body for security.
     * 
     * @param request contains obligationId
     * @return 200 OK with latest scoring
     */
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','SUPERVISOR','AUDITOR')")
    @PostMapping("/search/obligation")
    public ResponseEntity<ScoringSegmentationResponse> getLatestByObligation(@RequestBody ListScoringByObligationRequest request) {
        return ResponseEntity.ok(service.getLatestByObligation(request.obligationId()));
    }

    /**
     * List all scorings for a client (paginated in practice).
     * Note: clientId is encrypted in request body for security.
     * 
     * @param request contains clientId
     * @return 200 OK with scoring history
     */
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','SUPERVISOR','AUDITOR')")
    @PostMapping("/search/client")
    public ResponseEntity<List<ScoringSegmentationResponse>> listByClient(@RequestBody ListScoringByClientRequest request) {
        return ResponseEntity.ok(service.listByCustomer(request.clientId()));
    }
}

