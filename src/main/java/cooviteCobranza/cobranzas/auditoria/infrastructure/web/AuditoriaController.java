package cooviteCobranza.cobranzas.auditoria.infrastructure.web;

import cooviteCobranza.cobranzas.auditoria.application.dto.AuditoriaEventoResponse;
import cooviteCobranza.cobranzas.auditoria.application.dto.RegistrarAuditoriaRequest;
import cooviteCobranza.cobranzas.auditoria.application.service.AuditoriaApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuditoriaController {

    private final AuditoriaApplicationService service;

    public AuditoriaController(AuditoriaApplicationService service) {
        this.service = service;
    }

    @PostMapping("/eventos")
    public ResponseEntity<Void> registrar(@RequestBody RegistrarAuditoriaRequest request) {
        service.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/eventos/{entidad}/{entidadId}")
    public ResponseEntity<List<AuditoriaEventoResponse>> listarPorEntidad(@PathVariable String entidad,
                                                                          @PathVariable Long entidadId) {
        return ResponseEntity.ok(service.listarPorEntidad(entidad, entidadId));
    }
}

