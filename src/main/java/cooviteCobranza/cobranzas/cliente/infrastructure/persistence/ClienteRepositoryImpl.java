package cooviteCobranza.cobranzas.cliente.infrastructure.persistence;

import cooviteCobranza.cobranzas.cliente.domain.model.Cliente;
import cooviteCobranza.cobranzas.cliente.domain.repository.ClienteRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ClienteRepositoryImpl implements ClienteRepository {

    private final ClienteJpaRepository jpaRepository;

    public ClienteRepositoryImpl(ClienteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Cliente save(Cliente cliente) {
        ClienteJpaEntity entity = clienteToEntity(cliente);
        ClienteJpaEntity savedEntity = jpaRepository.save(entity);
        return entityToCliente(savedEntity);
    }

    @Override
    public Optional<Cliente> findById(Long id) {
        return jpaRepository.findById(id).map(this::entityToCliente);
    }

    @Override
    public Optional<Cliente> findByDocumento(String tipoDocumento, String numeroDocumento) {
        return jpaRepository.findByTipoDocumentoAndNumeroDocumento(tipoDocumento, numeroDocumento)
                .map(this::entityToCliente);
    }

    // Mappers
    private ClienteJpaEntity clienteToEntity(Cliente cliente) {
        return new ClienteJpaEntity(
                cliente.getId(),
                cliente.getTipoDocumento(),
                cliente.getNumeroDocumento(),
                cliente.getNombreCompleto(),
                cliente.getTelefono(),
                cliente.getEmail(),
                cliente.isAceptaWhatsApp(),
                cliente.isAceptaSms(),
                cliente.isAceptaEmail(),
                cliente.getUpdatedAt()
        );
    }

    private Cliente entityToCliente(ClienteJpaEntity entity) {
        return Cliente.reconstruir(
                entity.getId(),
                entity.getTipoDocumento(),
                entity.getNumeroDocumento(),
                entity.getNombreCompleto(),
                entity.getTelefono(),
                entity.getEmail(),
                entity.isAceptaWhatsApp(),
                entity.isAceptaSms(),
                entity.isAceptaEmail(),
                entity.getUpdatedAt()
        );
    }
}

