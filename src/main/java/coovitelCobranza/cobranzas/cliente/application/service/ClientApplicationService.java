package coovitelCobranza.cobranzas.cliente.application.service;

import coovitelCobranza.cobranzas.cliente.application.dto.UpdateContactClientRequest;
import coovitelCobranza.cobranzas.cliente.application.dto.UpdateConsentsClientRequest;
import coovitelCobranza.cobranzas.cliente.application.dto.ClientResponse;
import coovitelCobranza.cobranzas.cliente.application.dto.CreateClientRequest;
import coovitelCobranza.cobranzas.cliente.application.exception.ClientBusinessException;
import coovitelCobranza.cobranzas.cliente.application.exception.ClientNotFoundException;
import coovitelCobranza.cobranzas.cliente.domain.model.Client;
import coovitelCobranza.cobranzas.cliente.domain.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de aplicación para gestionar operaciones de clientes.
 * Coordina la interacción entre las solicitudes HTTP, la lógica de negocio
 * y la persistencia de datos.
 */
@Service
public class ClientApplicationService {

    private final ClientRepository clienteRepository;

    /**
     * Construye el servicio de aplicación de clientes.
     *
     * @param clienteRepository repositorio de acceso a datos de clientes
     */
    public ClientApplicationService(ClientRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    /**
     * Crea un nuevo cliente en el sistema.
     *
     * @param request datos para crear el cliente
     * @return respuesta con datos del cliente creado
     * @throws ClientBusinessException si el cliente ya existe o hay error en los datos
     */
    @Transactional
    public ClientResponse create(CreateClientRequest request) {
        try {
            var existingClient = clienteRepository.findByDocumento(request.tipoDocumento(), request.numeroDocumento());
            if (existingClient.isPresent()) {
                throw new ClientBusinessException(
                        "Client already exists with document: " + request.tipoDocumento() + " " + request.numeroDocumento()
                );
            }

            Client client = Client.create(
                    request.tipoDocumento(),
                    request.numeroDocumento(),
                    request.fullName()
            );

            if (request.telefono() != null || request.email() != null) {
                client.updateContact(request.telefono(), request.email());
            }

            Client savedClient = clienteRepository.save(client);
            return ClientResponse.fromDomain(savedClient);
        } catch (ClientBusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new ClientBusinessException("Error creating client", e);
        }
    }

    /**
     * Obtiene un cliente por su identificador único.
     *
     * @param id identificador del cliente
     * @return respuesta con datos del cliente
     * @throws ClientNotFoundException si el cliente no existe
     */
    @Transactional(readOnly = true)
    public ClientResponse getById(Long id) {
        Client client = clienteRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));
        return ClientResponse.fromDomain(client);
    }

    /**
     * Obtiene un cliente por tipo y número de documento.
     *
     * @param tipoDocumento tipo de documento del cliente
     * @param numeroDocumento número de documento del cliente
     * @return respuesta con datos del cliente
     * @throws ClientNotFoundException si el cliente no existe
     */
    @Transactional(readOnly = true)
    public ClientResponse getByDocument(String tipoDocumento, String numeroDocumento) {
        Client client = clienteRepository.findByDocumento(tipoDocumento, numeroDocumento)
                .orElseThrow(() -> new ClientNotFoundException(tipoDocumento, numeroDocumento));
        return ClientResponse.fromDomain(client);
    }

    /**
     * Actualiza la información de contacto de un cliente.
     *
     * @param id identificador del cliente
     * @param request datos de contacto a actualizar
     * @return respuesta con datos del cliente actualizado
     * @throws ClientNotFoundException si el cliente no existe
     */
    @Transactional
    public ClientResponse updateContact(Long id, UpdateContactClientRequest request) {
        Client client = clienteRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        client.updateContact(request.telefono(), request.email());
        Client updatedClient = clienteRepository.save(client);
        return ClientResponse.fromDomain(updatedClient);
    }

    /**
     * Actualiza los consentimientos de comunicación de un cliente.
     *
     * @param id identificador del cliente
     * @param request consentimientos a actualizar
     * @return respuesta con datos del cliente actualizado
     * @throws ClientNotFoundException si el cliente no existe
     */
    @Transactional
    public ClientResponse updateConsents(Long id, UpdateConsentsClientRequest request) {
        Client client = clienteRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        client.updateConsents(
                request.aceptaWhatsApp(),
                request.aceptaSms(),
                request.aceptaEmail()
        );
        Client updatedClient = clienteRepository.save(client);
        return ClientResponse.fromDomain(updatedClient);
    }
}

