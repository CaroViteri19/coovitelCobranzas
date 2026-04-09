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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para gestionar operaciones de clientes.
 * Expone endpoints para crear, buscar y actualizar información de clientes.
 */
@RestController
@RequestMapping("/api/v1/clients")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ClientController {

    private final ClientApplicationService clienteApplicationService;

    /**
     * Construye el controlador de clientes.
     *
     * @param clienteApplicationService servicio de aplicación para clientes
     */
    public ClientController(ClientApplicationService clienteApplicationService) {
        this.clienteApplicationService = clienteApplicationService;
    }

    /**
     * Crea un nuevo cliente.
     *
     * @param request datos del cliente a crear
     * @return ResponseEntity con status 201 y datos del cliente creado
     */
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR')")
    @PostMapping
    public ResponseEntity<ClientResponse> create(@RequestBody CreateClientRequest request) {
        ClientResponse response = clienteApplicationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Busca un cliente por su identificador único.
     *
     * @param request solicitud con el ID del cliente
     * @return ResponseEntity con status 200 y datos del cliente
     */
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR','AGENTE','AUDITOR')")
    @PostMapping("/search/id")
    public ResponseEntity<ClientResponse> getById(@RequestBody GetClientByIdRequest request) {
        ClientResponse response = clienteApplicationService.getById(request.clientId());
        return ResponseEntity.ok(response);
    }

    /**
     * Busca un cliente por tipo y número de documento.
     *
     * @param request solicitud con tipo y número de documento
     * @return ResponseEntity con status 200 y datos del cliente
     */
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR','AGENTE','AUDITOR')")
    @PostMapping("/search/document")
    public ResponseEntity<ClientResponse> getByDocument(@RequestBody GetClientByDocumentRequest request) {
        ClientResponse response = clienteApplicationService.getByDocument(
                request.documentType(),
                request.documentNumber()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Actualiza la información de contacto de un cliente.
     *
     * @param request datos de contacto a actualizar
     * @return ResponseEntity con status 200 y datos del cliente actualizado
     */
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR','AGENTE')")
    @PostMapping("/update/contact")
    public ResponseEntity<ClientResponse> updateContact(@RequestBody UpdateClientContactRequest request) {
        ClientResponse response = clienteApplicationService.updateContact(
                request.clientId(),
                new UpdateContactClientRequest(request.phone(), request.email())
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Actualiza los consentimientos de comunicación de un cliente.
     *
     * @param request consentimientos a actualizar
     * @return ResponseEntity con status 200 y datos del cliente actualizado
     */
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR','AGENTE')")
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

