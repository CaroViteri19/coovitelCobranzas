package coovitelCobranza.cobranzas.orquestacion.infrastructure.web;

import coovitelCobranza.cobranzas.orquestacion.application.dto.EnviarOrquestacionRequest;
import coovitelCobranza.cobranzas.orquestacion.application.dto.GetOrchestrationByIdRequest;
import coovitelCobranza.cobranzas.orquestacion.application.dto.ListOrchestrationByCaseRequest;
import coovitelCobranza.cobranzas.orquestacion.application.dto.OrquestacionEjecucionResponse;
import coovitelCobranza.cobranzas.orquestacion.application.service.OrquestacionApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orchestration")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrquestacionController {

    private final OrquestacionApplicationService service;

    public OrquestacionController(OrquestacionApplicationService service) {
        this.service = service;
    }

    @PostMapping("/send")
    public ResponseEntity<OrquestacionEjecucionResponse> send(@RequestBody EnviarOrquestacionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.enviar(request));
    }

    @PostMapping("/search/id")
    public ResponseEntity<OrquestacionEjecucionResponse> getById(@RequestBody GetOrchestrationByIdRequest request) {
        return ResponseEntity.ok(service.obtenerPorId(request.executionId()));
    }

    @PostMapping("/search/case")
    public ResponseEntity<List<OrquestacionEjecucionResponse>> listByCase(@RequestBody ListOrchestrationByCaseRequest request) {
        return ResponseEntity.ok(service.listarPorCasoGestion(request.caseId()));
    }
}

