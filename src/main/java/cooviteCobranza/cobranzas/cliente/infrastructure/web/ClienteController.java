package cooviteCobranza.cobranzas.cliente.infrastructure.web;

import cooviteCobranza.cobranzas.cliente.application.dto.ActualizarContactoClienteRequest;
import cooviteCobranza.cobranzas.cliente.application.dto.ActualizarConsentimientosClienteRequest;
import cooviteCobranza.cobranzas.cliente.application.dto.ClienteResponse;
import cooviteCobranza.cobranzas.cliente.application.dto.CrearClienteRequest;
import cooviteCobranza.cobranzas.cliente.application.service.ClienteApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ClienteController {

    private final ClienteApplicationService clienteApplicationService;

    public ClienteController(ClienteApplicationService clienteApplicationService) {
        this.clienteApplicationService = clienteApplicationService;
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> crear(@RequestBody CrearClienteRequest request) {
        ClienteResponse response = clienteApplicationService.crearCliente(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> obtenerPorId(@PathVariable Long id) {
        ClienteResponse response = clienteApplicationService.obtenerPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/documento/{tipoDocumento}/{numeroDocumento}")
    public ResponseEntity<ClienteResponse> obtenerPorDocumento(
            @PathVariable String tipoDocumento,
            @PathVariable String numeroDocumento) {
        ClienteResponse response = clienteApplicationService.obtenerPorDocumento(tipoDocumento, numeroDocumento);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/contacto")
    public ResponseEntity<ClienteResponse> actualizarContacto(
            @PathVariable Long id,
            @RequestBody ActualizarContactoClienteRequest request) {
        ClienteResponse response = clienteApplicationService.actualizarContacto(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/consentimientos")
    public ResponseEntity<ClienteResponse> actualizarConsentimientos(
            @PathVariable Long id,
            @RequestBody ActualizarConsentimientosClienteRequest request) {
        ClienteResponse response = clienteApplicationService.actualizarConsentimientos(id, request);
        return ResponseEntity.ok(response);
    }
}

