package coovitelCobranza.cobranzas.casogestion.infrastructure.web;

import coovitelCobranza.cobranzas.casogestion.application.dto.AssignAdvisorByCaseRequest;
import coovitelCobranza.cobranzas.casogestion.application.dto.CaseResponse;
import coovitelCobranza.cobranzas.casogestion.application.dto.CloseCaseRequest;
import coovitelCobranza.cobranzas.casogestion.application.dto.CreateCaseRequest;
import coovitelCobranza.cobranzas.casogestion.application.dto.GetCaseByIdRequest;
import coovitelCobranza.cobranzas.casogestion.application.dto.ScheduleActionByCaseRequest;
import coovitelCobranza.cobranzas.casogestion.application.dto.AssignAdvisorRequest;
import coovitelCobranza.cobranzas.casogestion.application.dto.ScheduleActionRequest;
import coovitelCobranza.cobranzas.casogestion.application.service.CaseApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST que expone operaciones para la gestión de casos de cobranza.
 *
 * Proporciona endpoints HTTP para crear, consultar, asignar asesores,
 * programar acciones y cerrar casos.
 */
@RestController
@RequestMapping("/api/v1/cases")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CaseController {

    private final CaseApplicationService caseApplicationService;

    /**
     * Construye el controlador con la dependencia del servicio.
     *
     * @param caseApplicationService el servicio de aplicación de casos
     */
    public CaseController(CaseApplicationService caseApplicationService) {
        this.caseApplicationService = caseApplicationService;
    }

    /**
     * Crea un nuevo caso de cobranza.
     *
     * @param request DTO con los datos del caso a crear
     * @return ResponseEntity con status 201 y el caso creado
     */
    @PostMapping
    public ResponseEntity<CaseResponse> create(@RequestBody CreateCaseRequest request) {
        CaseResponse response = caseApplicationService.createCase(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Obtiene un caso por su identificador.
     *
     * @param request DTO con el ID del caso a recuperar
     * @return ResponseEntity con status 200 y el caso solicitado
     */
    @PostMapping("/search/id")
    public ResponseEntity<CaseResponse> getById(@RequestBody GetCaseByIdRequest request) {
        CaseResponse response = caseApplicationService.getById(request.caseId());
        return ResponseEntity.ok(response);
    }

    /**
     * Lista todos los casos pendientes de gestión.
     *
     * @return ResponseEntity con status 200 y la lista de casos pendientes
     */
    @GetMapping("/pending")
    public ResponseEntity<List<CaseResponse>> listPending() {
        List<CaseResponse> response = caseApplicationService.listPending();
        return ResponseEntity.ok(response);
    }

    /**
     * Asigna un asesor a un caso existente.
     *
     * @param request DTO con el ID del caso y el nombre del asesor
     * @return ResponseEntity con status 200 y el caso actualizado
     */
    @PostMapping("/assign-advisor")
    public ResponseEntity<CaseResponse> assignAdvisor(@RequestBody AssignAdvisorByCaseRequest request) {
        CaseResponse response = caseApplicationService.assignAdvisor(
                request.caseId(),
                new AssignAdvisorRequest(request.advisor())
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Programa la próxima acción para un caso.
     *
     * @param request DTO con el ID del caso y la fecha de la próxima acción
     * @return ResponseEntity con status 200 y el caso actualizado
     */
    @PostMapping("/schedule-action")
    public ResponseEntity<CaseResponse> scheduleAction(@RequestBody ScheduleActionByCaseRequest request) {
        CaseResponse response = caseApplicationService.scheduleAction(
                request.caseId(),
                new ScheduleActionRequest(request.actionAt())
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Cierra un caso de cobranza marcándolo como resuelto.
     *
     * @param request DTO con el ID del caso a cerrar
     * @return ResponseEntity con status 200 y el caso cerrado
     */
    @PostMapping("/close")
    public ResponseEntity<CaseResponse> closeCase(@RequestBody CloseCaseRequest request) {
        CaseResponse response = caseApplicationService.closeCase(request.caseId());
        return ResponseEntity.ok(response);
    }
}

