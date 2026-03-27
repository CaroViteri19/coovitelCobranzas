package coovitelCobranza.cobranzas.cliente.infrastructure.web;

import coovitelCobranza.cobranzas.cliente.application.dto.ActualizarContactoClienteRequest;
import coovitelCobranza.cobranzas.cliente.application.dto.ActualizarConsentimientosClienteRequest;
import coovitelCobranza.cobranzas.cliente.application.dto.ClienteResponse;
import coovitelCobranza.cobranzas.cliente.application.dto.CrearClienteRequest;
import coovitelCobranza.cobranzas.cliente.application.dto.GetClientByDocumentRequest;
import coovitelCobranza.cobranzas.cliente.application.dto.GetClientByIdRequest;
import coovitelCobranza.cobranzas.cliente.application.dto.UpdateClientConsentsRequest;
import coovitelCobranza.cobranzas.cliente.application.dto.UpdateClientContactRequest;
import coovitelCobranza.cobranzas.cliente.application.service.ClienteApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clients")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ClienteController {

    private final ClienteApplicationService clienteApplicationService;

    public ClienteController(ClienteApplicationService clienteApplicationService) {
        this.clienteApplicationService = clienteApplicationService;
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> create(@RequestBody CrearClienteRequest request) {
        ClienteResponse response = clienteApplicationService.crearCliente(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/search/id")
    public ResponseEntity<ClienteResponse> getById(@RequestBody GetClientByIdRequest request) {
        ClienteResponse response = clienteApplicationService.obtenerPorId(request.clientId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search/document")
    public ResponseEntity<ClienteResponse> getByDocument(@RequestBody GetClientByDocumentRequest request) {
        ClienteResponse response = clienteApplicationService.obtenerPorDocumento(
                request.documentType(),
                request.documentNumber()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update/contact")
    public ResponseEntity<ClienteResponse> updateContact(@RequestBody UpdateClientContactRequest request) {
        ClienteResponse response = clienteApplicationService.actualizarContacto(
                request.clientId(),
                new ActualizarContactoClienteRequest(request.phone(), request.email())
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update/consents")
    public ResponseEntity<ClienteResponse> updateConsents(@RequestBody UpdateClientConsentsRequest request) {
        ClienteResponse response = clienteApplicationService.actualizarConsentimientos(
                request.clientId(),
                new ActualizarConsentimientosClienteRequest(
                        request.acceptsWhatsApp(),
                        request.acceptsSms(),
                        request.acceptsEmail()
                )
        );
        return ResponseEntity.ok(response);
    }
}

