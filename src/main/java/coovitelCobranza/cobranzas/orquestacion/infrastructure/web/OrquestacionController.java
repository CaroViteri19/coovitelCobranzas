package coovitelCobranza.cobranzas.orquestacion.infrastructure.web;

import coovitelCobranza.cobranzas.orquestacion.application.dto.EnviarOrquestacionRequest;
import coovitelCobranza.cobranzas.orquestacion.application.dto.OrquestacionEjecucionResponse;
import coovitelCobranza.cobranzas.orquestacion.application.service.OrquestacionApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orquestacion")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrquestacionController {

    private final OrquestacionApplicationService service;

    public OrquestacionController(OrquestacionApplicationService service) {
        this.service = service;
    }

    @PostMapping("/enviar")
    public ResponseEntity<OrquestacionEjecucionResponse> enviar(@RequestBody EnviarOrquestacionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.enviar(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrquestacionEjecucionResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @GetMapping("/caso/{casoGestionId}")
    public ResponseEntity<List<OrquestacionEjecucionResponse>> listarPorCaso(@PathVariable Long casoGestionId) {
        return ResponseEntity.ok(service.listarPorCasoGestion(casoGestionId));
    }
}

