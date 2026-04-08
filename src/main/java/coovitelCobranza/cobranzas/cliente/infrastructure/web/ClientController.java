package coovitelCobranza.cobranzas.cliente.infrastructure.web;

import coovitelCobranza.cobranzas.cliente.application.dto.UpdateContactClientRequest;
import coovitelCobranza.cobranzas.cliente.application.dto.UpdateConsentsClientRequest;
import coovitelCobranza.cobranzas.cliente.application.dto.ClientResponse;
import coovitelCobranza.cobranzas.cliente.application.dto.CreateClientRequest;
import coovitelCobranza.cobranzas.cliente.application.dto.GetClientByDocumentRequest;
import coovitelCobranza.cobranzas.cliente.application.dto.GetClientByIdRequest;
import coovitelCobranza.cobranzas.cliente.application.dto.UpdateClientConsentsRequest;
import coovitelCobranza.cobranzas.cliente.application.dto.UpdateClientContactRequest;
import coovitelCobranza.cobranzas.cliente.application.service.ClientApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clients")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ClientController {

    private final ClientApplicationService clienteApplicationService;

    public ClientController(ClientApplicationService clienteApplicationService) {
        this.clienteApplicationService = clienteApplicationService;
    }

    @PostMapping
    public ResponseEntity<ClientResponse> create(@RequestBody CreateClientRequest request) {
        ClientResponse response = clienteApplicationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/search/id")
    public ResponseEntity<ClientResponse> getById(@RequestBody GetClientByIdRequest request) {
        ClientResponse response = clienteApplicationService.getById(request.clientId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search/document")
    public ResponseEntity<ClientResponse> getByDocument(@RequestBody GetClientByDocumentRequest request) {
        ClientResponse response = clienteApplicationService.getByDocument(
                request.documentType(),
                request.documentNumber()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update/contact")
    public ResponseEntity<ClientResponse> updateContact(@RequestBody UpdateClientContactRequest request) {
        ClientResponse response = clienteApplicationService.updateContact(
                request.clientId(),
                new UpdateContactClientRequest(request.phone(), request.email())
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update/consents")
    public ResponseEntity<ClientResponse> updateConsents(@RequestBody UpdateClientConsentsRequest request) {
        ClientResponse response = clienteApplicationService.updateConsents(
                request.clientId(),
                new UpdateConsentsClientRequest(
                        request.acceptsWhatsApp(),
                        request.acceptsSms(),
                        request.acceptsEmail()
                )
        );
        return ResponseEntity.ok(response);
    }
}

