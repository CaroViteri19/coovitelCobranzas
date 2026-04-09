package coovitelCobranza.cobranzas.casogestion.infrastructure.web;

import coovitelCobranza.cobranzas.casogestion.application.dto.AssignAdvisorByCaseRequest;
import coovitelCobranza.cobranzas.casogestion.application.dto.AssignAdvisorRequest;
import coovitelCobranza.cobranzas.casogestion.application.dto.CaseResponse;
import coovitelCobranza.cobranzas.casogestion.application.dto.CloseCaseRequest;
import coovitelCobranza.cobranzas.casogestion.application.dto.CreateCaseRequest;
import coovitelCobranza.cobranzas.casogestion.application.dto.GetCaseByIdRequest;
import coovitelCobranza.cobranzas.casogestion.application.dto.ScheduleActionByCaseRequest;
import coovitelCobranza.cobranzas.casogestion.application.dto.ScheduleActionRequest;
import coovitelCobranza.cobranzas.casogestion.application.dto.TransitionCaseStatusRequest;
import coovitelCobranza.cobranzas.casogestion.application.service.CaseApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cases")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CaseController {

    private final CaseApplicationService caseApplicationService;

    public CaseController(CaseApplicationService caseApplicationService) {
        this.caseApplicationService = caseApplicationService;
    }

    @PostMapping
    public ResponseEntity<CaseResponse> create(@RequestBody CreateCaseRequest request) {
        CaseResponse response = caseApplicationService.createCase(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/search/id")
    public ResponseEntity<CaseResponse> getById(@RequestBody GetCaseByIdRequest request) {
        CaseResponse response = caseApplicationService.getById(request.caseId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<CaseResponse>> listPending() {
        List<CaseResponse> response = caseApplicationService.listPending();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/assign-advisor")
    public ResponseEntity<CaseResponse> assignAdvisor(@RequestBody AssignAdvisorByCaseRequest request) {
        CaseResponse response = caseApplicationService.assignAdvisor(
                request.caseId(),
                new AssignAdvisorRequest(
                        request.advisor(),
                        request.performedBy(),
                        request.performedByRole(),
                        request.assignmentSource(),
                        request.correlationId()
                )
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/schedule-action")
    public ResponseEntity<CaseResponse> scheduleAction(@RequestBody ScheduleActionByCaseRequest request) {
        CaseResponse response = caseApplicationService.scheduleAction(
                request.caseId(),
                new ScheduleActionRequest(request.actionAt())
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/transition-status")
    public ResponseEntity<CaseResponse> transitionStatus(@RequestBody TransitionCaseStatusRequest request) {
        CaseResponse response = caseApplicationService.transitionStatus(request.caseId(), request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/close")
    public ResponseEntity<CaseResponse> closeCase(@RequestBody CloseCaseRequest request) {
        CaseResponse response = caseApplicationService.closeCase(request.caseId());
        return ResponseEntity.ok(response);
    }
}
