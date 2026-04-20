package coovitelCobranza.cobranzas.client.infrastructure.web;

import coovitelCobranza.cobranzas.client.application.dto.UpdateContactClientRequest;
import coovitelCobranza.cobranzas.client.application.dto.UpdateConsentsClientRequest;
import coovitelCobranza.cobranzas.client.application.dto.ClientResponse;
import coovitelCobranza.cobranzas.client.application.dto.CreateClientRequest;
import coovitelCobranza.cobranzas.client.application.dto.GetClientByDocumentRequest;
import coovitelCobranza.cobranzas.client.application.dto.GetClientByIdRequest;
import coovitelCobranza.cobranzas.client.application.dto.ListClientsRequest;
import coovitelCobranza.cobranzas.client.application.dto.PageResponse;
import coovitelCobranza.cobranzas.client.application.dto.UpdateClientConsentsRequest;
import coovitelCobranza.cobranzas.client.application.dto.UpdateClientContactRequest;
import coovitelCobranza.cobranzas.client.application.service.ClientApplicationService;
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
     * Lista clientes paginados (por defecto 10 por página, máx 100).
     *
     * @param request criterio de paginación {page,size}
     * @return ResponseEntity con status 200 y página de clientes
     */
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','SUPERVISOR','AGENTE','AUDITOR')")
    @PostMapping("/list")
    public ResponseEntity<PageResponse<ClientResponse>> list(@RequestBody(required = false) ListClientsRequest request) {
        ListClientsRequest safe = request != null ? request : new ListClientsRequest(0, 10);
        PageResponse<ClientResponse> response = clienteApplicationService.listAll(safe.safePage(), safe.safeSize());
        return ResponseEntity.ok(response);
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

