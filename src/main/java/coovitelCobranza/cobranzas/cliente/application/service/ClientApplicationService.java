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

@Service
public class ClientApplicationService {

    private final ClientRepository clienteRepository;

    public ClientApplicationService(ClientRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

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

    @Transactional(readOnly = true)
    public ClientResponse getById(Long id) {
        Client client = clienteRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));
        return ClientResponse.fromDomain(client);
    }

    @Transactional(readOnly = true)
    public ClientResponse getByDocument(String tipoDocumento, String numeroDocumento) {
        Client client = clienteRepository.findByDocumento(tipoDocumento, numeroDocumento)
                .orElseThrow(() -> new ClientNotFoundException(tipoDocumento, numeroDocumento));
        return ClientResponse.fromDomain(client);
    }

    @Transactional
    public ClientResponse updateContact(Long id, UpdateContactClientRequest request) {
        Client client = clienteRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        client.updateContact(request.telefono(), request.email());
        Client updatedClient = clienteRepository.save(client);
        return ClientResponse.fromDomain(updatedClient);
    }

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

