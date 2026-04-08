package coovitelCobranza.cobranzas.cliente.infrastructure.persistence.repository;

import coovitelCobranza.cobranzas.cliente.domain.model.Client;
import coovitelCobranza.cobranzas.cliente.domain.repository.ClientRepository;
import coovitelCobranza.cobranzas.cliente.infrastructure.persistence.entity.ClientJpaEntity;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ClientRepositoryImpl implements ClientRepository {

    private final ClientJpaRepository jpaRepository;

    public ClientRepositoryImpl(ClientJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Client save(Client cliente) {
        ClientJpaEntity entity = clienteToEntity(cliente);
        ClientJpaEntity savedEntity = jpaRepository.save(entity);
        return entityToClient(savedEntity);
    }

    @Override
    public Optional<Client> findById(Long id) {
        return jpaRepository.findById(id).map(this::entityToClient);
    }

    @Override
    public Optional<Client> findByDocumento(String tipoDocumento, String numeroDocumento) {
        return jpaRepository.findByTipoDocumentoAndNumeroDocumento(tipoDocumento, numeroDocumento)
                .map(this::entityToClient);
    }

    // Mappers
    private ClientJpaEntity clienteToEntity(Client cliente) {
        return new ClientJpaEntity(
                cliente.getId(),
                cliente.getTipoDocumento(),
                cliente.getNumeroDocumento(),
                cliente.getFullName(),
                cliente.getTelefono(),
                cliente.getEmail(),
                cliente.isAceptaWhatsApp(),
                cliente.isAceptaSms(),
                cliente.isAceptaEmail(),
                cliente.getUpdatedAt()
        );
    }

    private Client entityToClient(ClientJpaEntity entity) {
        return Client.reconstruct(
                entity.getId(),
                entity.getTipoDocumento(),
                entity.getNumeroDocumento(),
                entity.getFullName(),
                entity.getTelefono(),
                entity.getEmail(),
                entity.isAceptaWhatsApp(),
                entity.isAceptaSms(),
                entity.isAceptaEmail(),
                entity.getUpdatedAt()
        );
    }
}

