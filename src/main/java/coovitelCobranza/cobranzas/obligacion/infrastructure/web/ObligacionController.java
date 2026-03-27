package coovitelCobranza.cobranzas.obligacion.infrastructure.web;

import coovitelCobranza.cobranzas.obligacion.application.dto.AplicarPagoRequest;
import coovitelCobranza.cobranzas.obligacion.application.dto.ObligacionResponse;
import coovitelCobranza.cobranzas.obligacion.application.dto.RegistrarMoraRequest;
import coovitelCobranza.cobranzas.obligacion.application.service.ObligacionApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/obligaciones")
@Tag(name = "Obligaciones", description = "Gestion de obligaciones de cartera")
public class ObligacionController {

    private final ObligacionApplicationService obligacionApplicationService;

    public ObligacionController(ObligacionApplicationService obligacionApplicationService) {
        this.obligacionApplicationService = obligacionApplicationService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar obligacion por id")
    public ResponseEntity<ObligacionResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(obligacionApplicationService.obtenerPorId(id));
    }

    @GetMapping("/numero/{numeroObligacion}")
    @Operation(summary = "Consultar obligacion por numero")
    public ResponseEntity<ObligacionResponse> obtenerPorNumero(@PathVariable String numeroObligacion) {
        return ResponseEntity.ok(obligacionApplicationService.obtenerPorNumero(numeroObligacion));
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar obligaciones por cliente")
    public ResponseEntity<List<ObligacionResponse>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(obligacionApplicationService.listarPorCliente(clienteId));
    }

    @PutMapping("/{id}/mora")
    @Operation(summary = "Registrar o actualizar la mora de una obligacion")
    public ResponseEntity<ObligacionResponse> registrarMora(@PathVariable Long id,
                                                            @Valid @RequestBody RegistrarMoraRequest request) {
        return ResponseEntity.ok(obligacionApplicationService.registrarMora(id, request.diasMora(), request.saldoVencido()));
    }

    @PutMapping("/{id}/pago")
    @Operation(summary = "Aplicar pago sobre una obligacion")
    public ResponseEntity<ObligacionResponse> aplicarPago(@PathVariable Long id,
                                                          @Valid @RequestBody AplicarPagoRequest request) {
        return ResponseEntity.ok(obligacionApplicationService.aplicarPago(id, request.valorPago()));
    }
}

