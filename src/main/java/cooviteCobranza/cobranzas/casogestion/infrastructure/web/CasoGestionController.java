package cooviteCobranza.cobranzas.casogestion.infrastructure.web;

import cooviteCobranza.cobranzas.casogestion.application.dto.AsignarAsesorRequest;
import cooviteCobranza.cobranzas.casogestion.application.dto.CasoGestionResponse;
import cooviteCobranza.cobranzas.casogestion.application.dto.CrearCasoGestionRequest;
import cooviteCobranza.cobranzas.casogestion.application.dto.ProgramarAccionRequest;
import cooviteCobranza.cobranzas.casogestion.application.service.CasoGestionApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/casos-gestion")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CasoGestionController {

    private final CasoGestionApplicationService casoGestionApplicationService;

    public CasoGestionController(CasoGestionApplicationService casoGestionApplicationService) {
        this.casoGestionApplicationService = casoGestionApplicationService;
    }

    @PostMapping
    public ResponseEntity<CasoGestionResponse> crear(@RequestBody CrearCasoGestionRequest request) {
        CasoGestionResponse response = casoGestionApplicationService.crearCaso(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CasoGestionResponse> obtenerPorId(@PathVariable Long id) {
        CasoGestionResponse response = casoGestionApplicationService.obtenerPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<CasoGestionResponse>> listarPendientes() {
        List<CasoGestionResponse> response = casoGestionApplicationService.listarPendientes();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/asesor")
    public ResponseEntity<CasoGestionResponse> asignarAsesor(
            @PathVariable Long id,
            @RequestBody AsignarAsesorRequest request) {
        CasoGestionResponse response = casoGestionApplicationService.asignarAsesor(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/proximo-accion")
    public ResponseEntity<CasoGestionResponse> programarAccion(
            @PathVariable Long id,
            @RequestBody ProgramarAccionRequest request) {
        CasoGestionResponse response = casoGestionApplicationService.programarAccion(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/cerrar")
    public ResponseEntity<CasoGestionResponse> cerrarCaso(@PathVariable Long id) {
        CasoGestionResponse response = casoGestionApplicationService.cerrarCaso(id);
        return ResponseEntity.ok(response);
    }
}

