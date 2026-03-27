package cooviteCobranza.cobranzas.interaccion.infrastructure.web;

import cooviteCobranza.cobranzas.interaccion.application.dto.ActualizarResultadoInteraccionRequest;
import cooviteCobranza.cobranzas.interaccion.application.dto.CrearInteraccionRequest;
import cooviteCobranza.cobranzas.interaccion.application.dto.InteraccionResponse;
import cooviteCobranza.cobranzas.interaccion.application.service.InteraccionApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interacciones")
@CrossOrigin(origins = "*", maxAge = 3600)
public class InteraccionController {

    private final InteraccionApplicationService interaccionApplicationService;

    public InteraccionController(InteraccionApplicationService interaccionApplicationService) {
        this.interaccionApplicationService = interaccionApplicationService;
    }

    @PostMapping
    public ResponseEntity<InteraccionResponse> crear(@RequestBody CrearInteraccionRequest request) {
        InteraccionResponse response = interaccionApplicationService.crearInteraccion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InteraccionResponse> obtenerPorId(@PathVariable Long id) {
        InteraccionResponse response = interaccionApplicationService.obtenerPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/caso/{casoGestionId}")
    public ResponseEntity<List<InteraccionResponse>> listarPorCasoGestion(@PathVariable Long casoGestionId) {
        List<InteraccionResponse> response = interaccionApplicationService.listarPorCasoGestion(casoGestionId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/resultado")
    public ResponseEntity<InteraccionResponse> actualizarResultado(
            @PathVariable Long id,
            @RequestBody ActualizarResultadoInteraccionRequest request) {
        InteraccionResponse response = interaccionApplicationService.actualizarResultado(id, request);
        return ResponseEntity.ok(response);
    }
}

