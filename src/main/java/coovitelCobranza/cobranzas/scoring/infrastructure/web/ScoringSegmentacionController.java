package coovitelCobranza.cobranzas.scoring.infrastructure.web;

import coovitelCobranza.cobranzas.scoring.application.dto.CalcularScoringRequest;
import coovitelCobranza.cobranzas.scoring.application.dto.ScoringSegmentacionResponse;
import coovitelCobranza.cobranzas.scoring.application.service.ScoringSegmentacionApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scoring-segmentacion")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ScoringSegmentacionController {

    private final ScoringSegmentacionApplicationService applicationService;

    public ScoringSegmentacionController(ScoringSegmentacionApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/calcular")
    public ResponseEntity<ScoringSegmentacionResponse> calcular(@RequestBody CalcularScoringRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(applicationService.calcular(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScoringSegmentacionResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.obtenerPorId(id));
    }

    @GetMapping("/obligacion/{obligacionId}")
    public ResponseEntity<ScoringSegmentacionResponse> obtenerUltimoPorObligacion(@PathVariable Long obligacionId) {
        return ResponseEntity.ok(applicationService.obtenerUltimoPorObligacion(obligacionId));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ScoringSegmentacionResponse>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(applicationService.listarPorCliente(clienteId));
    }
}

