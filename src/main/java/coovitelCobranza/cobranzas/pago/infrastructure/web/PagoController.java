package coovitelCobranza.cobranzas.pago.infrastructure.web;

import coovitelCobranza.cobranzas.pago.application.dto.ConfirmarPagoRequest;
import coovitelCobranza.cobranzas.pago.application.dto.CrearPagoRequest;
import coovitelCobranza.cobranzas.pago.application.dto.PagoResponse;
import coovitelCobranza.cobranzas.pago.application.service.PagoApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PagoController {

    private final PagoApplicationService pagoApplicationService;

    public PagoController(PagoApplicationService pagoApplicationService) {
        this.pagoApplicationService = pagoApplicationService;
    }

    @PostMapping
    public ResponseEntity<PagoResponse> crear(@RequestBody CrearPagoRequest request) {
        PagoResponse response = pagoApplicationService.crearPago(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoResponse> obtenerPorId(@PathVariable Long id) {
        PagoResponse response = pagoApplicationService.obtenerPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/referencia/{referenciaExterna}")
    public ResponseEntity<PagoResponse> obtenerPorReferencia(@PathVariable String referenciaExterna) {
        PagoResponse response = pagoApplicationService.obtenerPorReferencia(referenciaExterna);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/obligacion/{obligacionId}")
    public ResponseEntity<List<PagoResponse>> listarPorObligacion(@PathVariable Long obligacionId) {
        List<PagoResponse> response = pagoApplicationService.listarPorObligacion(obligacionId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirmar")
    public ResponseEntity<PagoResponse> confirmarPago(@RequestBody ConfirmarPagoRequest request) {
        PagoResponse response = pagoApplicationService.confirmarPago(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/rechazar")
    public ResponseEntity<PagoResponse> rechazarPago(@PathVariable Long id) {
        PagoResponse response = pagoApplicationService.rechazarPago(id);
        return ResponseEntity.ok(response);
    }
}

